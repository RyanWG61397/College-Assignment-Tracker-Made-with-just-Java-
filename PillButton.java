package com.ryangordon.collegetracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class PillButton extends JButton {

    private Color fill = new Color(104, 160, 84);      // default green
    private Color fillHover = new Color(94, 148, 76);  // slightly darker
    private Color fillPressed = new Color(82, 134, 66);// even darker
    private Color border = new Color(72, 118, 58);     // subtle border
    private int arc = 18;

    public PillButton(String text) {
        super(text);

        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 12f));

        // Turn off default button painting (we'll paint it ourselves)
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        // Make the text feel centered in the pill
        setMargin(new Insets(6, 14, 6, 14));
        setPreferredSize(new Dimension(135, 30));

        // Cursor looks nicer on desktop
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setPillColors(Color fill, Color border) {
        this.fill = fill;
        this.border = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ButtonModel m = getModel();
        Color bg = fill;
        if (m.isPressed()) bg = fillPressed;
        else if (m.isRollover()) bg = fillHover;

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        g2.setPaint(new GradientPaint(
                0, 0, new Color(255, 255, 255, 80),
                0, h / 2f, new Color(255, 255, 255, 0)
        ));

        g2.fillRoundRect(0, 0, w, h / 2, arc, arc);
        g2.setClip(null);

        // Border
        g2.setColor(border);
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g); // draws the text
    }
}
