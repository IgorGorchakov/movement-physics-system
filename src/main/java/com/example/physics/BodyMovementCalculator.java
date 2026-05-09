package com.example.physics;

import com.example.config.RenderConfig;

import java.util.ArrayList;
import java.util.List;

import static com.example.config.PhysicsConfig.CULL_MARGIN;
import static com.example.config.PhysicsConfig.G;
import static com.example.config.PhysicsConfig.MIN_DIST_SQ;

public class BodyMovementCalculator {

    private final List<Body> bodies = new ArrayList<>();
    private final Object simLock = new Object();
    private final RK4Buffers buf = new RK4Buffers();

    public void addBody(Body b) {
        synchronized (simLock) {
            bodies.add(b);
        }
    }

    public List<BodySnapshot> snapshot() {
        synchronized (simLock) {
            List<BodySnapshot> out = new ArrayList<>(bodies.size());
            for (Body b : bodies) {
                out.add(BodySnapshot.of(b));
            }
            return out;
        }
    }

    /**
     * Advances the simulation by one time step using the classical
     * fourth-order Runge-Kutta integrator (RK4).
     *
     * <p>The state of body {@code i} is the vector
     * {@code y_i = (x_i, y_i, vx_i, vy_i)}. Newton's second law gives
     * the system of ODEs:
     * <pre>
     *     dx_i/dt  = vx_i
     *     dy_i/dt  = vy_i
     *     dvx_i/dt = a_x(x, y)            // see {@link #calculateAccelerations}
     *     dvy_i/dt = a_y(x, y)
     * </pre>
     *
     * <p>For a generic ODE {@code dy/dt = f(t, y)}, RK4 advances by step
     * size {@code h} as:
     * <pre>
     *     k1 = f(t,         y)
     *     k2 = f(t + h/2,   y + (h/2) * k1)
     *     k3 = f(t + h/2,   y + (h/2) * k2)
     *     k4 = f(t + h,     y + h     * k3)
     *
     *     y(t + h) = y(t) + (h / 6) * (k1 + 2*k2 + 2*k3 + k4)
     * </pre>
     *
     * <p>The (1, 2, 2, 1) weighting is Simpson's rule applied to the
     * derivative; local truncation error is {@code O(h^5)}, global
     * error {@code O(h^4)}.
     *
     * <p>After integrating, bodies that have drifted more than
     * {@code CULL_MARGIN} beyond the world bounds are removed.
     *
     * @param dt time step {@code h}, in simulation time units.
     */
    public void calculateTimeStep(double dt) {
        synchronized (simLock) {
            int n = bodies.size();
            if (n == 0) return;
            buf.ensureCapacity(n);

            for (int i = 0; i < n; i++) {
                Body b = bodies.get(i);
                buf.m[i] = b.mass;
                buf.x[i] = b.x;
                buf.y[i] = b.y;
                buf.vx[i] = b.vx;
                buf.vy[i] = b.vy;
            }

            calculateAccelerations(n, buf.m, buf.x, buf.y, buf.k1vx, buf.k1vy);

            calculateTrialStateAtOffset(n, 0.5 * dt, buf.x, buf.y, buf.vx, buf.vy, buf.vx, buf.vy,
                    buf.k1vx, buf.k1vy, buf.tx, buf.ty, buf.tvx, buf.tvy);
            calculateAccelerations(n, buf.m, buf.tx, buf.ty, buf.k2vx, buf.k2vy);
            copy(n, buf.tvx, buf.tvy, buf.k2x, buf.k2y);

            calculateTrialStateAtOffset(n, 0.5 * dt, buf.x, buf.y, buf.vx, buf.vy, buf.tvx, buf.tvy,
                    buf.k2vx, buf.k2vy, buf.tx, buf.ty, buf.tvx, buf.tvy);
            calculateAccelerations(n, buf.m, buf.tx, buf.ty, buf.k3vx, buf.k3vy);
            copy(n, buf.tvx, buf.tvy, buf.k3x, buf.k3y);

            calculateTrialStateAtOffset(n, dt, buf.x, buf.y, buf.vx, buf.vy, buf.tvx, buf.tvy,
                    buf.k3vx, buf.k3vy, buf.tx, buf.ty, buf.tvx, buf.tvy);
            calculateAccelerations(n, buf.m, buf.tx, buf.ty, buf.k4vx, buf.k4vy);
            copy(n, buf.tvx, buf.tvy, buf.k4x, buf.k4y);

            for (int i = 0; i < n; i++) {
                Body b = bodies.get(i);
                double k1x = buf.vx[i], k1y = buf.vy[i];
                b.x += dt / 6.0 * (k1x + 2 * buf.k2x[i] + 2 * buf.k3x[i] + buf.k4x[i]);
                b.y += dt / 6.0 * (k1y + 2 * buf.k2y[i] + 2 * buf.k3y[i] + buf.k4y[i]);
                b.vx += dt / 6.0 * (buf.k1vx[i] + 2 * buf.k2vx[i] + 2 * buf.k3vx[i] + buf.k4vx[i]);
                b.vy += dt / 6.0 * (buf.k1vy[i] + 2 * buf.k2vy[i] + 2 * buf.k3vy[i] + buf.k4vy[i]);
            }

            for (int i = bodies.size() - 1; i >= 0; i--) {
                Body b = bodies.get(i);
                if (b.x < -CULL_MARGIN || b.x > RenderConfig.WIDTH + CULL_MARGIN
                        || b.y < -CULL_MARGIN || b.y > RenderConfig.HEIGHT + CULL_MARGIN) {
                    bodies.remove(i);
                }
            }
        }
    }

    /**
     * Single Euler-style sub-step used inside RK4.
     *
     * <p>For each body {@code i}, given a base state {@code (x, y, vx, vy)},
     * an input velocity {@code v_in} and an input acceleration {@code a_in},
     * compute the trial state at offset {@code h}:
     * <pre>
     *     x_out  = x_base  + h * vx_in
     *     y_out  = y_base  + h * vy_in
     *     vx_out = vx_base + h * ax_in
     *     vy_out = vy_base + h * ay_in
     * </pre>
     *
     * <p>This is the generic explicit Euler update {@code y_new = y + h * f(y)}
     * applied component-wise. RK4 calls this three times with different
     * {@code (v_in, a_in)} pairs to evaluate {@code k2}, {@code k3}, and
     * {@code k4}.
     *
     * <p>{@code vIn} and {@code vxOut} (likewise {@code vInY} / {@code vyOut})
     * may alias the same array — index {@code i} is read before written
     * within a single iteration, so in-place updates are safe.
     */
    private static void calculateTrialStateAtOffset(int n, double h,
                                                    double[] xBase, double[] yBase,
                                                    double[] vxBase, double[] vyBase,
                                                    double[] vIn, double[] vInY,
                                                    double[] axIn, double[] ayIn,
                                                    double[] xOut, double[] yOut,
                                                    double[] vxOut, double[] vyOut) {
        for (int i = 0; i < n; i++) {
            double vxi = vIn[i], vyi = vInY[i];
            xOut[i] = xBase[i] + h * vxi;
            yOut[i] = yBase[i] + h * vyi;
            vxOut[i] = vxBase[i] + h * axIn[i];
            vyOut[i] = vyBase[i] + h * ayIn[i];
        }
    }

    /**
     * Computes the gravitational acceleration vector on every body —
     * the analytical spatial derivative of the Newtonian gravitational
     * potential {@code U(r) = -G * m_i * m_j / r}.
     *
     * <p>For each body {@code i}, Newton's law of universal gravitation
     * Component-wise (2D):
     * <pre>
     *     a_x_i = G * Σ_j m_j * (x_j - x_i) / r_ij^3
     *     a_y_i = G * Σ_j m_j * (y_j - y_i) / r_ij^3
     *     where r_ij = sqrt((x_j - x_i)^2 + (y_j - y_i)^2)
     * </pre>
     *
     * <p>This is equivalent to {@code a_i = -∇_i U / m_i}: the gradient
     * (spatial differentiation) of the potential, divided by the body's
     * own mass to yield acceleration.
     *
     * <p>Implementation notes:
     * <ul>
     *   <li>Newton's third law is exploited: each pair {@code (i, j)} is
     *       visited once and the equal-and-opposite contribution is added
     *       to both bodies. Cost is O(n^2 / 2).</li>
     *   <li>The singular {@code 1/r^2} force is regularized by clamping
     *       {@code r^2 >= MIN_DIST_SQ}. This is equivalent to using the
     *       Plummer-softened potential {@code U = -G*m_i*m_j / sqrt(r^2 + ε^2)}
     *       with {@code ε^2 = MIN_DIST_SQ}.</li>
     *   <li>{@code invR3 = 1 / (r^2 * sqrt(r^2)) = 1/r^3} avoids one
     *       division per pair.</li>
     * </ul>
     *
     * @param n  number of bodies.
     * @param m  mass array, length >= n.
     * @param px x-positions to evaluate the field at (may be a trial
     *           state during RK4 sub-steps, not necessarily the live
     *           body positions).
     * @param py y-positions, paired with {@code px}.
     * @param ax output: x-components of acceleration {@code a_x_i}.
     * @param ay output: y-components of acceleration {@code a_y_i}.
     */
    private void calculateAccelerations(int n, double[] m,
                                        double[] px, double[] py,
                                        double[] ax, double[] ay) {
        for (int i = 0; i < n; i++) {
            ax[i] = 0;
            ay[i] = 0;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = px[j] - px[i];
                double dy = py[j] - py[i];
                double r2 = dx * dx + dy * dy;
                if (r2 < MIN_DIST_SQ) r2 = MIN_DIST_SQ;
                double invR3 = 1.0 / (r2 * Math.sqrt(r2));
                ax[i] += G * m[j] * dx * invR3;
                ay[i] += G * m[j] * dy * invR3;
                ax[j] -= G * m[i] * dx * invR3;
                ay[j] -= G * m[i] * dy * invR3;
            }
        }
    }

    private void copy(int n, double[] sx, double[] sy, double[] dx, double[] dy) {
        System.arraycopy(sx, 0, dx, 0, n);
        System.arraycopy(sy, 0, dy, 0, n);
    }
}
