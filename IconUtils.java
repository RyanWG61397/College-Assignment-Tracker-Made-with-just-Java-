package com.ryangordon.collegetracker.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconUtils {

    public static ImageIcon recolor(ImageIcon icon, Color color) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw original
        g2.drawImage(icon.getImage(), 0, 0, null);

        // recolor
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.setColor(color);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
        return new ImageIcon(image);
    }
}
