package com.ryangordon.collegetracker.ui.theme;

import javax.swing.*;
import java.awt.*;

public class PremiumPanel extends JPanel {
    private final Color top;
    private final Color bottom;

    public PremiumPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Base gradient
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRect(0, 0, w, h);

        // Top “glimmer” highlight line (subtle)
        g2.setColor(new Color(255, 255, 255, 18));
        g2.drawLine(0, 0, w, 0);

        // Bottom inner shadow line
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
        super.paintComponent(g);
    }
}
