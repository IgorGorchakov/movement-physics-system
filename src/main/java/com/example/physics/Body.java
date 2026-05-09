package com.example.physics;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicLong;

public class Body {
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    public final long id;
    public double x, y, vx, vy, mass;
    public final Color color;

    public Body(double x, double y, double mass, double vx, double vy, Color color) {
        this.id = NEXT_ID.getAndIncrement();
        this.x = x; this.y = y; this.mass = mass;
        this.vx = vx; this.vy = vy; this.color = color;
    }
}
