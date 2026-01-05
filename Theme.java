package com.ryangordon.collegetracker.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public final class Theme {

    // === Core palette (Obsidian Steel) ===
    public static final Color BG            = new Color(0x12, 0x14, 0x17); // app background
    public static final Color PANEL         = new Color(0x1B, 0x1F, 0x26); // cards/panels
    public static final Color PANEL_ALT     = new Color(32, 36, 44);        // slightly different from PANEL
    public static final Color PANEL_2       = new Color(0x22, 0x28, 0x34); // secondary layer
    public static final Color TEXT          = new Color(0xE6, 0xE8, 0xEB); // primary text
    public static final Color TEXT_MUTED    = new Color(0xA0, 0xA6, 0xB0); // secondary text

    // public static final Color SELECTION     = new Color(55, 90, 140);
    // public static final Color DIVIDER       = new Color(60, 60, 70);

    // public static final Color BG = new Color(18, 20, 26);

    // public static final Color PANEL = new Color(28, 31, 40);
    // public static final Color PANEL_2 = new Color(34, 38, 50);
    // public static final Color PANEL_ALT = new Color(30, 33, 43);

    public static final Color DIVIDER = new Color(70, 75, 90);
    public static final Color SELECTION = new Color(55, 90, 160);

    public static final Color SIDEBAR_BG_TOP = new Color(30, 34, 46);
    public static final Color SIDEBAR_BG_BOT = new Color(18, 20, 28);

    public static final Color SIDEBAR_TEXT = new Color(235, 235, 240);
    public static final Color SIDEBAR_TEXT_DIM = new Color(185, 190, 205);
    public static final Color SIDEBAR_SELECTED_TOP = new Color(70, 95, 150);
    public static final Color SIDEBAR_SELECTED_BOT = new Color(45, 65, 110);

    // Accent (dominant amber/orange)
    public static final Color ACCENT        = new Color(0xFF, 0x8A, 0x1F);
    public static final Color ACCENT_HOVER  = new Color(0xFF, 0x9A, 0x3F);
    public static final Color ACCENT_PRESSED= new Color(0xE8, 0x76, 0x12);

    // Sidebar
    public static final Color SIDEBAR_BUTTON        = new Color(70, 90, 125);
    public static final Color SIDEBAR_BUTTON_HOVER     = new Color(60, 78, 110);
    public static final Color SIDEBAR_SELECTED  = new Color(48, 64, 92);

    // Alternating column colors for TableCellRenderer
    public static final Color ROW_DARK = new Color(0x1E1E1E);
    public static final Color ROW_LIGHT = new Color(0x252525);
    public static final Color ROW_SELECTED = new Color(0x2D3A4A);

    private Theme() {}

    public static void install() {
        // 1) Install FlatLaf (dark)
        FlatDarkLaf.setup();

        // 2) Global defaults (FlatLaf reads these UI defaults)
        UIManager.put("Panel.background", PANEL);
        UIManager.put("RootPane.background", BG);

        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.disabledForeground", TEXT_MUTED);

        // Buttons
        UIManager.put("Button.arc", 16);
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);

        // Focus glow: subtle, “premium”
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.focusColor", new Color(0xFF, 0x8A, 0x1F, 120));

        // Scrollbars: slimmer
        UIManager.put("ScrollBar.width", 10);

        // Tables: we’ll fine-tune more in Step 4, but set nice defaults
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.selectionBackground", new Color(0x2A, 0x33, 0x44));
        UIManager.put("Table.selectionForeground", TEXT);

        // Header styling
        UIManager.put("TableHeader.height", 34);
        UIManager.put("TableHeader.background", PANEL_2);
        UIManager.put("TableHeader.foreground", TEXT_MUTED);

        // Combo boxes
        UIManager.put("ComboBox.buttonStyle", "button");
        UIManager.put("ComboBox.padding", new Insets(6, 10, 6, 10));

        // Tooltips (nice touch)
        UIManager.put("ToolTip.background", PANEL_2);
        UIManager.put("ToolTip.foreground", TEXT);
    }

    public static void applyRowStriping(
            JComponent c, JTable table, int row, boolean isSelected
    ) {
        if (isSelected) {
            c.setBackground(Theme.ROW_SELECTED);
        } else {
            c.setBackground(row % 2 == 0
                    ? Theme.ROW_DARK
                    : Theme.ROW_LIGHT);
        }
    }
}
