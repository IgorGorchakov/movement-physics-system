package com.example.physics.model;

import java.util.concurrent.atomic.AtomicLong;

public class BodyIdentifier {
    private static final AtomicLong GENERATOR = new AtomicLong(1);

    public static long getNext() {
        return GENERATOR.getAndIncrement();
    }
}
