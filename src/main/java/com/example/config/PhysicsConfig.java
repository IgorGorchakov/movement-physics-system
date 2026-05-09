package com.example.config;

import com.example.physics.model.BodyFactory;

public final class PhysicsConfig {
    private PhysicsConfig() {}

    /**
     * Gravitational constant. Scales the strength of gravitational interaction between bodies.
     * Used in {@link com.example.physics.BodyMovementCalculator#calculateAccelerations}.
     */
    public static final double G = 60.0;

    /**
     * Simulation time per frame. The total physics timestep advanced each frame before
     * yielding to the render thread. Combined with {@link #SUBSTEPS} to determine the
     * per-substep interval: {@code subDt = DT / SUBSTEPS}.
     */
    public static final double DT = 0.05;

    /**
     * Plummer softening length. Prevents numerical singularities when two bodies approach
     * each other closely — equivalent to clamping the gravitational potential to
     * {@code U = -G·m_i·m_j / sqrt(r² + ε²)}. See {@link #MIN_DIST_SQ} for the squared form.
     */
    public static final double MIN_DIST = 12.0;

    /**
     * Squared Plummer softening length ({@code MIN_DIST²}). Used directly in the acceleration
     * calculation to avoid a {@code Math.sqrt} on the clamping check. Bodies with
     * {@code r² < MIN_DIST_SQ} are treated as if they were {@code MIN_DIST} apart.
     */
    public static final double MIN_DIST_SQ = MIN_DIST * MIN_DIST;

    /**
     * Number of RK4 substeps per frame. Each frame, the physics thread advances the simulation
     * by {@link #DT} total time in {@code SUBSTEPS} equal increments. More substeps improve
     * numerical stability and trajectory accuracy at the cost of CPU.
     */
    public static final int SUBSTEPS = 24;

    /**
     * Distance beyond the world bounds (the 800×800 canvas) at which a body is considered
     * lost and removed from the simulation. Bodies that drift more than {@code CULL_MARGIN}
     * pixels outside the canvas are culled to keep the body count manageable.
     */
    public static final double CULL_MARGIN = 4000.0;

    /**
     * Base speed for randomly spawned bodies (click-to-spawn). The actual speed is randomized
     * in the range {@code [SPAWN_SPEED × 0.5, SPAWN_SPEED × 1.5]}.
     */
    public static final double SPAWN_SPEED = 1.0;

    /**
     * Minimum mass for a newly spawned body. Used by {@link BodyFactory}.
     */
    public static final double SPAWN_MASS_MIN = 40.0;

    /**
     * Maximum mass for a newly spawned body. Used by {@link BodyFactory}.
     */
    public static final double SPAWN_MASS_MAX = 120.0;
}
