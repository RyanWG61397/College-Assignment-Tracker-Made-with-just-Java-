package com.ryangordon.collegetracker.ui.util;

import javax.swing.*;
import java.awt.*;

public class TrashIcon implements Icon {
    private final int size;
    private final Color color;

    public TrashIcon(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        int w = size;
        int h = size;

        // Simple trash can shape
        int lidY = y + 3;
        int bodyY = y + 5;

        // Lid
        g2.fillRoundRect(x + 4, lidY, w - 8, 2, 2, 2);
        // Handle
        g2.fillRoundRect(x + 6, y + 1, w - 12, 3, 2, 2);

        // Body outline
        g2.drawRoundRect(x + 4, bodyY, w - 8, h - 7, 3, 3);

        // Slats
        g2.drawLine(x + 7, bodyY + 2, x + 7, y + h - 3);
        g2.drawLine(x + 8, bodyY + 2, x + 8, y + h - 3);
        g2.drawLine(x + 11, bodyY + 2, x + 11, y + h - 3);
        g2.drawLine(x + 12, bodyY + 2, x + 12, y + h - 3);

        g2.dispose();
    }
}
