package com.example.config;

public final class RenderConfig {
    private RenderConfig() {}

    /**
     * Canvas width in pixels. The {@link javax.swing.JFrame} is fixed to this size.
     * Used as the base dimension in {@link com.example.ui.PanelRenderer#PanelRenderer}.
     */
    public static final int WIDTH = 800;

    /**
     * Canvas height in pixels. Paired with {@link #WIDTH} to set the fixed frame size.
     */
    public static final int HEIGHT = 800;

    /**
     * Maximum number of points stored in each body's circular trail ring buffer.
     * A trail with more points than this produces longer visual paths. Consumed by
     * {@link com.example.render.SimulationRenderer} when pushing positions and
     * drawing trail segments.
     */
    public static final int TRAIL_LENGTH = 600;

    /**
     * Alpha value of the semi-transparent black overlay drawn over the canvas each frame.
     * Higher values cause trails to fade faster; lower values let trails persist longer.
     * Drawn in {@link com.example.render.SimulationRenderer#renderToCanvas}.
     */
    public static final int FADE_ALPHA = 25;

    /**
     * Line width (stroke) used when drawing trail segments.
     * Applied via {@link java.awt.Graphics2D#setStroke} in
     * {@link com.example.render.SimulationRenderer}.
     */
    public static final float TRAIL_STROKE = 2f;

    /**
     * Opacity of the semi-transparent outer halo (glow) drawn around each body.
     * Combined with {@link #BODY_GLOW_PADDING} to produce a soft, glowing appearance.
     * Drawn in {@link com.example.render.SimulationRenderer}.
     */
    public static final int BODY_GLOW_ALPHA = 60;

    /**
     * Extra radius (in pixels) added to the body's solid circle to create the glow halo.
     * The glow is drawn as a larger, semi-transparent circle behind the body.
     * See {@link #BODY_GLOW_ALPHA}.
     */
    public static final int BODY_GLOW_PADDING = 6;

    /**
     * Interval in milliseconds between successive render ticks. A {@link javax.swing.Timer}
     * fires on the EDT at this rate (~60 fps) to call
     * {@link com.example.ui.PanelRenderer#renderToCanvas} and {@code repaint()}.
     */
    public static final int REDRAW_INTERVAL_MS = 16;

    /**
     * Scaling factor applied to the mouse drag delta to compute launch velocity.
     * When the user drags from point A to B, the body's initial velocity is
     * {@code (B.x - A.x) * DRAG_VELOCITY_SCALE, (B.y - A.y) * DRAG_VELOCITY_SCALE},
     * then clamped to {@link #MAX_DRAG_SPEED}. Used by
     * {@link com.example.ui.DragController}.
     */
    public static final double DRAG_VELOCITY_SCALE = 0.025;

    /**
     * Maximum launch speed for a body spawned via click-and-drag. The raw velocity
     * computed from the drag distance (see {@link #DRAG_VELOCITY_SCALE}) is clamped
     * to this value. Prevents bodies from being launched at unreasonably high speeds.
     * Used by {@link com.example.ui.DragController}.
     */
    public static final double MAX_DRAG_SPEED = 12.0;

    /**
     * Minimum drag distance (in pixels) between {@code mousePressed} and {@code mouseReleased}
     * to classify the gesture as a drag rather than a click. Distinguishes "tap to spawn
     * with random velocity" from "drag to aim". Used by {@link com.example.ui.DragController}.
     */
    public static final int CLICK_DRAG_THRESHOLD = 4;

    /**
     * Length of the arrowhead drawn on the drag overlay. The dashed launch-arrow is drawn
     * from the spawn point in the direction of the drag, with an arrowhead of this length
     * at the tip. Used by {@link com.example.render.DragOverlayRenderer}.
     */
    public static final int DRAG_ARROW_HEAD = 8;

    /**
     * Dash segment length for the dashed line pattern used to draw the drag arrow.
     * Applied via {@link java.awt.BasicStroke} in {@link com.example.render.DragOverlayRenderer}.
     * Paired with the default dash gap to produce the segmented arrow appearance.
     */
    public static final float DRAG_DASH = 6f;
}
