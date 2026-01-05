package com.ryangordon.collegetracker.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StripedProgressBarUI extends BasicProgressBarUI {

    private final Color base;
    private final Color stripe;

    public StripedProgressBarUI(Color base, Color stripe) {
        this.base = base;
        this.stripe = stripe;
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = c.getWidth();
        int h = c.getHeight();

        // Rounded track
        int arc = h; // full pill rounding
        Shape track = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);

        // Track/background
        g2.setColor(progressBar.getBackground());
        g2.fill(track);

        // Filled width
        int fillW = (int) Math.round(w * (progressBar.getPercentComplete()));
        if (fillW > 0) {
            Shape fill = new RoundRectangle2D.Float(0, 0, fillW, h, arc, arc);

            // Base fill
            g2.setClip(fill);
            g2.setColor(base);
            g2.fillRect(0, 0, fillW, h);

            // Diagonal stripes
            g2.setColor(stripe);
            int stripeWidth = 14; // tweak
            int gap = 10;         // tweak
            for (int x = -h; x < fillW + h; x += (stripeWidth + gap)) {
                g2.fillPolygon(
                        new int[]{x, x + stripeWidth, x + stripeWidth + h, x + h},
                        new int[]{0, 0, h, h},
                        4
                );
            }

            g2.setClip(null);

            // Optional: subtle glossy highlight (top fade)
            g2.setClip(fill);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.18f));
            g2.setColor(Color.white);
            g2.fillRoundRect(0, 0, fillW, h / 2, arc, arc);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setClip(null);
        }

        g2.dispose();
    }
}
