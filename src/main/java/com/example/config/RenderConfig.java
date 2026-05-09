package com.example.config;

public final class RenderConfig {
    private RenderConfig() {}

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    public static final int TRAIL_LENGTH = 600;
    public static final int FADE_ALPHA = 25;
    public static final float TRAIL_STROKE = 2f;
    public static final int BODY_GLOW_ALPHA = 60;
    public static final int BODY_GLOW_PADDING = 6;

    public static final int REDRAW_INTERVAL_MS = 16;

    public static final double DRAG_VELOCITY_SCALE = 0.025;
    public static final double MAX_DRAG_SPEED = 12.0;
    public static final int CLICK_DRAG_THRESHOLD = 4;
    public static final int DRAG_ARROW_HEAD = 8;
    public static final float DRAG_DASH = 6f;
}
