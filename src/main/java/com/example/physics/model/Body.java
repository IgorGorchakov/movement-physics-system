package com.example.physics.model;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicLong;

public class Body {
    public final long id;
    public double x, y, vx, vy, mass;
    public final Color color;

    public Body(long id, double x, double y, double mass, double vx, double vy, Color color) {
        this.id = id;
        this.x = x; this.y = y; this.mass = mass;
        this.vx = vx; this.vy = vy; this.color = color;
    }
}
