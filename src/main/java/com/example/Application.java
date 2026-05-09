package com.example;

import com.example.config.RenderConfig;
import com.example.physics.PhysicsLoop;
import com.example.ui.PanelRenderer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class Application {

    static void main(String[] args) {
        SwingUtilities.invokeLater(Application::launch);
    }

    private static void launch() {
        PanelRenderer panel = new PanelRenderer();

        JFrame frame = new JFrame("Physical-Body Movement Simulation");
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        PhysicsLoop physics = new PhysicsLoop(panel.simulation());
        physics.start();

        new Timer(RenderConfig.REDRAW_INTERVAL_MS, e -> {
            panel.renderer().renderToCanvas(panel.simulation());
            panel.repaint();
        }).start();
    }
}
