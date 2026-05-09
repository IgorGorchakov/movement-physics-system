package com.example.physics;

import java.awt.Color;
import java.util.Random;

import static com.example.config.PhysicsConfig.SPAWN_MASS_MAX;
import static com.example.config.PhysicsConfig.SPAWN_MASS_MIN;
import static com.example.config.PhysicsConfig.SPAWN_SPEED;
import static com.example.config.RenderConfig.DRAG_VELOCITY_SCALE;
import static com.example.config.RenderConfig.MAX_DRAG_SPEED;

public class BodyFactory {

    private final Random rng;

    public BodyFactory() {
        this(new Random());
    }

    public BodyFactory(Random rng) {
        this.rng = rng;
    }

    public double randomMass() {
        return SPAWN_MASS_MIN + rng.nextDouble() * (SPAWN_MASS_MAX - SPAWN_MASS_MIN);
    }

    public Color randomColor() {
        return Color.getHSBColor(rng.nextFloat(), 0.85f, 1.0f);
    }

    public Body createRandom(int x, int y, double mass, Color color) {
        double angle = rng.nextDouble() * 2 * Math.PI;
        double speed = SPAWN_SPEED * (0.5 + rng.nextDouble());
        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);
        return new Body(x, y, mass, vx, vy, color);
    }

    public Body createAimed(int x, int y, int dx, int dy, double mass, Color color) {
        double vx = dx * DRAG_VELOCITY_SCALE;
        double vy = dy * DRAG_VELOCITY_SCALE;
        double speed = Math.hypot(vx, vy);
        if (speed > MAX_DRAG_SPEED) {
            double s = MAX_DRAG_SPEED / speed;
            vx *= s;
            vy *= s;
        }
        return new Body(x, y, mass, vx, vy, color);
    }
}
