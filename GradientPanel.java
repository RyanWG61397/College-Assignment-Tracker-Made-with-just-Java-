package com.ryangordon.collegetracker.ui;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    private final Color leftColor;
    private final Color rightColor;

    public GradientPanel(Color leftColor, Color rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        GradientPaint paint = new GradientPaint(
                0, 0, leftColor,
                w * 0.6f, 0, rightColor // fade out midway
        );

        g2.setPaint(paint);
        g2.fillRect(0, 0, w, h);
        g2.dispose();

        super.paintComponent(g);
    }
}