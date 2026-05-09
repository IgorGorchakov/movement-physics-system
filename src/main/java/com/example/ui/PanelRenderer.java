package com.example.ui;

import com.example.config.RenderConfig;
import com.example.physics.BodyFactory;
import com.example.physics.BodyMovementCalculator;
import com.example.render.DragOverlayRenderer;
import com.example.render.SimulationRenderer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PanelRenderer extends JPanel {

    private final BodyMovementCalculator sim = new BodyMovementCalculator();
    private final SimulationRenderer renderer = new SimulationRenderer();
    private final DragController dragController = new DragController(sim, new BodyFactory());
    private final DragOverlayRenderer dragOverlay = new DragOverlayRenderer();

    public BodyMovementCalculator simulation() { return sim; }
    public SimulationRenderer renderer() { return renderer; }

    public PanelRenderer() {
        setPreferredSize(new Dimension(RenderConfig.WIDTH, RenderConfig.HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        dragController.attach(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(renderer.canvas(), 0, 0, null);
        if (dragController.isDragging()) {
            dragOverlay.draw((Graphics2D) g, dragController.snapshot());
        }
    }
}
