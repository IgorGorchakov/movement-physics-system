package com.example.physics.model;

import java.awt.Color;

public record BodySnapshot(long id, double x, double y, double mass, Color color) {

    public static BodySnapshot of(Body b) {
        return new BodySnapshot(b.id, b.x, b.y, b.mass, b.color);
    }
}
