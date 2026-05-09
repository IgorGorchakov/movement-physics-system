package com.example.config;

public final class PhysicsConfig {
    private PhysicsConfig() {}

    public static final double G = 60.0;
    public static final double DT = 0.05;
    public static final double MIN_DIST = 12.0;
    public static final double MIN_DIST_SQ = MIN_DIST * MIN_DIST;
    public static final int SUBSTEPS = 24;
    public static final double CULL_MARGIN = 4000.0;

    public static final double SPAWN_SPEED = 1.0;
    public static final double SPAWN_MASS_MIN = 40.0;
    public static final double SPAWN_MASS_MAX = 120.0;
}
