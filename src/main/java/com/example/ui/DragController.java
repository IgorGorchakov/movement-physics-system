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
    private volatile int dragStartX, dragStartY, dragCurX, dragCurY;
    private double pendingMass;
    private Color pendingColor;

    public DragController(BodyMovementCalculator sim, BodyFactory factory) {
        this.sim = sim;
        this.factory = factory;
    }

    public void attach(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = dragCurX = e.getX();
                dragStartY = dragCurY = e.getY();
                pendingMass = factory.randomMass();
                pendingColor = factory.randomColor();
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) return;
                dragging = false;
                int dx = e.getX() - dragStartX;
                int dy = e.getY() - dragStartY;
                Body body;
                if (Math.abs(dx) < CLICK_DRAG_THRESHOLD && Math.abs(dy) < CLICK_DRAG_THRESHOLD) {
                    body = factory.createRandom(dragStartX, dragStartY, pendingMass, pendingColor);
                } else {
                    body = factory.createAimed(dragStartX, dragStartY, dx, dy, pendingMass, pendingColor);
                }
                sim.addBody(body);
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dragCurX = e.getX();
                dragCurY = e.getY();
            }
        });
    }

    public boolean isDragging() {
        return dragging;
    }

    public DragState snapshot() {
        return new DragState(dragStartX, dragStartY, dragCurX, dragCurY, pendingMass, pendingColor);
    }
}
