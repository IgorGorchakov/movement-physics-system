package com.example.render;

import com.example.physics.BodyMovementCalculator;
import com.example.physics.BodySnapshot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.config.RenderConfig.BODY_GLOW_ALPHA;
import static com.example.config.RenderConfig.BODY_GLOW_PADDING;
import static com.example.config.RenderConfig.FADE_ALPHA;
import static com.example.config.RenderConfig.HEIGHT;
import static com.example.config.RenderConfig.TRAIL_LENGTH;
import static com.example.config.RenderConfig.TRAIL_STROKE;
import static com.example.config.RenderConfig.WIDTH;

public class SimulationRenderer {

    private final BufferedImage canvas =
            new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

    private final Map<Long, Trail> trails = new HashMap<>();

    public SimulationRenderer() {
        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.dispose();
    }

    public BufferedImage canvas() {
        return canvas;
    }

    public void renderToCanvas(BodyMovementCalculator sim) {
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0, 0, 0, FADE_ALPHA));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        List<BodySnapshot> bodies = sim.snapshot();
        sampleTrails(bodies);

        g.setStroke(new BasicStroke(TRAIL_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (BodySnapshot b : bodies) {
            Trail t = trails.get(b.id());
            if (t == null || t.size() < 2) continue;
            g.setColor(b.color());
            int prevX = (int) Math.round(t.xAt(0));
            int prevY = (int) Math.round(t.yAt(0));
            for (int k = 1; k < t.size(); k++) {
                int cx = (int) Math.round(t.xAt(k));
                int cy = (int) Math.round(t.yAt(k));
                g.drawLine(prevX, prevY, cx, cy);
                prevX = cx; prevY = cy;
            }
        }

        for (BodySnapshot b : bodies) {
            int radius = (int) Math.max(4, Math.sqrt(b.mass()) * 2);
            int bx = (int) Math.round(b.x());
            int by = (int) Math.round(b.y());
            Color c = b.color();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), BODY_GLOW_ALPHA));
            g.fillOval(bx - radius - BODY_GLOW_PADDING, by - radius - BODY_GLOW_PADDING,
                    (radius + BODY_GLOW_PADDING) * 2, (radius + BODY_GLOW_PADDING) * 2);
            g.setColor(c);
            g.fillOval(bx - radius, by - radius, radius * 2, radius * 2);
        }
        g.dispose();
    }

    private void sampleTrails(List<BodySnapshot> bodies) {
        Set<Long> alive = new HashSet<>();
        for (BodySnapshot b : bodies) {
            alive.add(b.id());
            trails.computeIfAbsent(b.id(), k -> new Trail(TRAIL_LENGTH)).push(b.x(), b.y());
        }
        trails.keySet().removeIf(id -> !alive.contains(id));
    }
}
