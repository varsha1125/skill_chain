package com.skillchain.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Aesthetic styling and UI design tokens for Skill Chain Swing Desktop Application.
 * Employs clean modern palette, generous whitespace, and readable typography.
 */
public final class UITheme {

    // Color Palette
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);      // Royal Blue #2563EB
    public static final Color COLOR_PRIMARY_DARK = new Color(29, 78, 216); // Darker Blue #1D4ED8
    public static final Color COLOR_SECONDARY = new Color(79, 70, 229);    // Indigo #4F46E5
    public static final Color COLOR_BG = new Color(248, 250, 252);         // Slate-50 #F8FAFC
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);  // Slate-900 #0F172A
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Slate-500 #64748B
    public static final Color COLOR_BORDER = new Color(226, 232, 240);     // Slate-200 #E2E8F0
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);     // Emerald-500 #10B981
    public static final Color COLOR_DANGER = new Color(239, 68, 68);       // Rose-500 #EF4444

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private UITheme() {
    }

    /**
     * Styles standard action buttons with rounded background and hover transitions.
     */
    public static JButton createPrimaryButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.PRIMARY);
    }

    /**
     * Styles secondary/outline buttons.
     */
    public static JButton createSecondaryButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.SECONDARY);
    }

    /**
     * Styles danger buttons (e.g. Delete, Reject).
     */
    public static JButton createDangerButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.DANGER);
    }

    /**
     * Configures input fields with consistent padding and clean borders.
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setForeground(COLOR_TEXT_PRIMARY);
        textField.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    /**
     * Creates a card panel with standard padding and subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                // Subtle drop shadow outline
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12));
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        return panel;
    }
}
