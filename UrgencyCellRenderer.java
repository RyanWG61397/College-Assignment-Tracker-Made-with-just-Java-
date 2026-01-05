package com.ryangordon.collegetracker.ui;

import com.ryangordon.collegetracker.ui.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public class UrgencyCellRenderer extends DefaultTableCellRenderer {

    private static final int ARC = 12;     // smaller roundness (mockup-like)
    private static final int PAD_X = 10;   // smaller pill padding
    private static final int PAD_Y = 4;

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
    ) {
        String text = value == null ? "" : value.toString().trim().toUpperCase();

        PillLabel label = new PillLabel(text);

        label.setOpaque(true);
        label.setBackground(row % 2 == 0 ? Theme.PANEL : Theme.PANEL_ALT);

        // Match mockup vibe: cleaner font, slightly smaller, not super-bold
        Font base = table.getFont();
        Font f = new Font("SansSerif", Font.PLAIN, Math.max(11, base.getSize()));
        // optional: tiny tracking / letter spacing feel (Swing doesn't truly support it everywhere)
        label.setFont(f);

        label.setHorizontalAlignment(SwingConstants.CENTER);

        // If selected, keep selection visuals
        if (isSelected) {
            label.setSelected(true);
            label.setSelectionColors(table.getSelectionBackground(), table.getSelectionForeground());
            return label;
        }

        // Softer, closer-to-mockup colors
        switch (text) {
            case "OVERDUE":
            case "URGENT":
                label.setPillTheme(
                        new Color(214, 95, 95),     // soft red
                        new Color(188, 74, 74),     // border red
                        Color.WHITE
                );
                break;

            case "MEDIUM":
            case "NORMAL":
                label.setPillTheme(
                        new Color(238, 206, 110),   // soft yellow
                        new Color(209, 175, 82),    // border yellow
                        new Color(70, 60, 30)       // dark text
                );
                break;

            case "LOW":
            case "HAVE TIME":
                label.setPillTheme(
                        new Color(120, 186, 120),   // soft green
                        new Color(92, 153, 92),     // border green
                        Color.WHITE
                );
                break;

            default:
                label.setPillTheme(
                        new Color(220, 220, 220),
                        new Color(200, 200, 200),
                        new Color(60, 60, 60)
                );
        }

        return label;
    }

    /**
     * Paints a smaller centered "pill" inside the table cell (closer to mockup).
     */
    private static class PillLabel extends JLabel {
        private Color fill;
        private Color border;
        private Color text;
        private boolean selected = false;
        private Color selBg;
        private Color selFg;

        PillLabel(String txt) {
            super(txt);
            setOpaque(false);
        }

        void setPillTheme(Color fill, Color border, Color text) {
            this.fill = fill;
            this.border = border;
            this.text = text;
            setForeground(text);
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        void setSelectionColors(Color bg, Color fg) {
            this.selBg = bg;
            this.selFg = fg;
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (selected) {
                // Let Swing draw selection normally (simple and consistent)
                g2.setColor(selBg);
                g2.fillRect(0, 0, w, h);
                super.paintComponent(g2);
                g2.dispose();
                return;
            }

            // Compute a smaller pill width based on text, centered
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textW = fm.stringWidth(getText());
            int pillW = textW + PAD_X * 2;
            int pillH = fm.getHeight() + PAD_Y * 2;

            // Clamp pill size so it doesn't touch cell edges
            pillW = Math.min(pillW, w - 12);
            pillH = Math.min(pillH, h - 6);

            int x = (w - pillW) / 2;
            int y = (h - pillH) / 2;

            // Fill
            g2.setColor(fill);
            g2.fillRoundRect(x, y, pillW, pillH, ARC, ARC);

            // Border (thin + subtle)
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(border);
            g2.drawRoundRect(x, y, pillW, pillH, ARC, ARC);

            // Text
            g2.setColor(text);
            int textX = (w - textW) / 2;
            int textY = y + (pillH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }
}