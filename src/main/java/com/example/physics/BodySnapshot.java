package com.example.physics;

import java.awt.Color;

public record BodySnapshot(long id, double x, double y, double mass, Color color) {
    static BodySnapshot of(Body b) {
        return new BodySnapshot(b.id, b.x, b.y, b.mass, b.color);
    }
}
