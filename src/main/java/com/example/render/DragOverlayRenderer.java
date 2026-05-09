package com.example.render;

import com.example.ui.DragState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import static com.example.config.RenderConfig.DRAG_ARROW_HEAD;
import static com.example.config.RenderConfig.DRAG_DASH;
import static com.example.config.RenderConfig.DRAG_VELOCITY_SCALE;
import static com.example.config.RenderConfig.MAX_DRAG_SPEED;

public class DragOverlayRenderer {

    public void draw(Graphics2D parent, DragState state) {
        Graphics2D g2 = (Graphics2D) parent.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int sx = state.startX(), sy = state.startY(), cx = state.curX(), cy = state.curY();
        int radius = (int) Math.max(4, Math.sqrt(state.mass()) * 2);
        Color c = state.color();
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 160));
        g2.fillOval(sx - radius, sy - radius, radius * 2, radius * 2);

        double rawSpeed = Math.hypot(cx - sx, cy - sy) * DRAG_VELOCITY_SCALE;
        double t = Math.min(1.0, rawSpeed / MAX_DRAG_SPEED);
        float hue = (float) (0.33 * (1.0 - t));
        g2.setColor(Color.getHSBColor(hue, 0.9f, 1.0f));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1f, new float[]{DRAG_DASH, DRAG_DASH}, 0f));
        g2.drawLine(sx, sy, cx, cy);
        double ang = Math.atan2(cy - sy, cx - sx);
        int x1 = (int) (cx - DRAG_ARROW_HEAD * Math.cos(ang - Math.PI / 6));
        int y1 = (int) (cy - DRAG_ARROW_HEAD * Math.sin(ang - Math.PI / 6));
        int x2 = (int) (cx - DRAG_ARROW_HEAD * Math.cos(ang + Math.PI / 6));
        int y2 = (int) (cy - DRAG_ARROW_HEAD * Math.sin(ang + Math.PI / 6));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(cx, cy, x1, y1);
        g2.drawLine(cx, cy, x2, y2);
        g2.dispose();
    }
}
