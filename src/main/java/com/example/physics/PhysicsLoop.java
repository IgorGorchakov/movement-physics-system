package com.example.physics;

import static com.example.config.PhysicsConfig.DT;
import static com.example.config.PhysicsConfig.SUBSTEPS;

public class PhysicsLoop {

    private final BodyMovementCalculator sim;
    private final Thread thread;
    private volatile boolean running = false;

    public PhysicsLoop(BodyMovementCalculator sim) {
        this.sim = sim;
        this.thread = new Thread(this::run, "physics");
        this.thread.setDaemon(true);
    }

    public void start() {
        running = true;
        thread.start();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    private void run() {
        final double subDt = DT / SUBSTEPS;
        final long stepNanos = (long) (DT * 1_000_000_000L / 4);
        long next = System.nanoTime();
        while (running) {
            for (int s = 0; s < SUBSTEPS; s++) sim.calculateTimeStep(subDt);
            next += stepNanos;
            long sleep = next - System.nanoTime();
            if (sleep > 0) {
                try { Thread.sleep(sleep / 1_000_000L, (int) (sleep % 1_000_000L)); }
                catch (InterruptedException e) { return; }
            } else {
                next = System.nanoTime();
            }
        }
    }
}
