package com.ryangordon.collegetracker.ui.theme;

import com.ryangordon.collegetracker.ui.util.IconUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SidebarButton extends JButton {

    private final ImageIcon baseIcon;

    public SidebarButton(String text, ImageIcon icon) {
        super(text);
        this.baseIcon = icon;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIcon(IconUtils.recolor(baseIcon, new Color(230, 235, 245)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean selected = Boolean.TRUE.equals(getClientProperty("selected"));
        boolean hover = getModel().isRollover();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 14;

        Shape rr = new RoundRectangle2D.Float(6, 4, w - 12, h - 8, arc, arc);

        // --- Base colors ---
        Color baseBlue = Theme.SIDEBAR_BUTTON;           // normal
        Color hoverBlue = Theme.SIDEBAR_BUTTON_HOVER;    // hover
        Color selectedBlue = Theme.SIDEBAR_SELECTED;     // selected

        Color fill =
                selected ? selectedBlue :
                        hover ? hoverBlue :
                                baseBlue;

        // --- Fill ---
        g2.setColor(fill);
        g2.fill(rr);

        // --- Subtle glossy highlight (keeps the premium look) ---
        g2.setClip(rr);
        g2.setPaint(new GradientPaint(
                0, 0, new Color(255, 255, 255, selected ? 40 : 55),
                0, h / 2f, new Color(255, 255, 255, 0)
        ));
        g2.fillRect(0, 0, w, h / 2);
        g2.setClip(null);

        // --- Outline (solid blue, no transparency) ---
        g2.setColor(fill.darker());
        g2.draw(rr);

        // --- Text color (always readable) ---
        if (selected) {
            setForeground(Color.WHITE);
        } else if (hover) {
            setForeground(Color.WHITE);
        } else {
            setForeground(new Color(230, 235, 245)); // soft white
        }

        if (selected || hover) {
            setIcon(IconUtils.recolor(baseIcon, Color.WHITE));
        } else {
            setIcon(IconUtils.recolor(baseIcon, new Color(240, 235, 245)));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
