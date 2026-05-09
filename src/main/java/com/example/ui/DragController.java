package com.example.ui;

import com.example.physics.Body;
import com.example.physics.BodyFactory;
import com.example.physics.BodyMovementCalculator;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static com.example.config.RenderConfig.CLICK_DRAG_THRESHOLD;

public class DragController {

    private final BodyMovementCalculator sim;
    private final BodyFactory factory;

    private volatile boolean dragging = false;
    private volatile DragState dragState;

    public DragController(BodyMovementCalculator sim, BodyFactory factory) {
        this.sim = sim;
        this.factory = factory;
    }

    public void attach(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int px = e.getX(), py = e.getY();
                dragState = new DragState(px, py, px, py, factory.randomMass(), factory.randomColor());
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) return;
                dragging = false;
                DragState s = dragState;
                int dx = e.getX() - s.startX();
                int dy = e.getY() - s.startY();
                Body body;
                if (Math.abs(dx) < CLICK_DRAG_THRESHOLD && Math.abs(dy) < CLICK_DRAG_THRESHOLD) {
                    body = factory.createRandom(s.startX(), s.startY(), s.mass(), s.color());
                } else {
                    body = factory.createAimed(s.startX(), s.startY(), dx, dy, s.mass(), s.color());
                }
                sim.addBody(body);
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DragState s = dragState;
                // Only update if still dragging — user may have released between events
                if (dragging) {
                    dragState = new DragState(s.startX(), s.startY(), e.getX(), e.getY(), s.mass(), s.color());
                }
            }
        });
    }

    public boolean isDragging() {
        return dragging;
    }

    /**
     * Returns a consistent snapshot of the current drag state.
     * The volatile reference guarantees a point-in-time view — all
     * fields in the returned DragState are from the same EDT write.
     */
    public DragState snapshot() {
        return dragState;
    }
}
