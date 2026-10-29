package com.skillchain.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Shared visual language for Skill Chain.
 *
 * The interface uses restrained neo-brutalism: warm paper, ink-black outlines,
 * flat blocks of color, and hard shadows. The style is intentionally graphic
 * and tactile instead of relying on gradients, glass effects, or soft pills.
 */
public final class UITheme {

    public static final Color COLOR_PRIMARY = new Color(255, 219, 71);      // butter yellow
    public static final Color COLOR_PRIMARY_DARK = new Color(232, 188, 0);
    public static final Color COLOR_SECONDARY = new Color(91, 211, 255);    // electric cyan
    public static final Color COLOR_ACCENT = new Color(255, 120, 184);      // hot pink
    public static final Color COLOR_BG = new Color(255, 248, 232);          // warm paper
    public static final Color COLOR_CARD_BG = new Color(255, 255, 255);
    public static final Color COLOR_TEXT_PRIMARY = new Color(24, 24, 24);
    public static final Color COLOR_TEXT_MUTED = new Color(77, 77, 77);
    public static final Color COLOR_BORDER = new Color(24, 24, 24);
    public static final Color COLOR_SUCCESS = new Color(96, 218, 137);
    public static final Color COLOR_DANGER = new Color(255, 113, 113);
    public static final Color COLOR_INK = new Color(24, 24, 24);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);

    private UITheme() {
    }

    public static JButton createPrimaryButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.PRIMARY);
    }

    public static JButton createSecondaryButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.SECONDARY);
    }

    public static JButton createDangerButton(String text) {
        return new ModernButton(text, ModernButton.ButtonStyle.DANGER);
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setForeground(COLOR_TEXT_PRIMARY);
        textField.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 2), new EmptyBorder(8, 12, 8, 12)));
    }

    /** Creates a flat card with a physical, offset ink shadow. */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                g2.setColor(COLOR_INK);
                g2.fillRect(6, 6, Math.max(0, w - 6), Math.max(0, h - 6));
                g2.setColor(COLOR_CARD_BG);
                g2.fillRect(0, 0, Math.max(0, w - 7), Math.max(0, h - 7));
                g2.setColor(COLOR_INK);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRect(1, 1, Math.max(0, w - 9), Math.max(0, h - 9));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        return panel;
    }
}
