package com.skillchain.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernTextField: Custom styled text field with rounded corners, interactive
 * focus ring, placeholder support, and generous comfortable padding.
 */
public class ModernTextField extends JTextField {

    private String placeholder = "";
    private boolean isFocused = false;
    private final int cornerRadius = 8;

    public ModernTextField(int columns) {
        super(columns);
        init();
    }

    public ModernTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(new Color(15, 23, 42)); // Slate-900
        setCaretColor(new Color(37, 99, 235));
        setBorder(new EmptyBorder(8, 14, 8, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));

        // Border: Focused (Blue) vs Normal (Slate-300)
        if (isFocused) {
            g2.setColor(new Color(37, 99, 235));
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));

        // Call super to paint actual text & caret
        super.paintComponent(g);

        // Draw placeholder if empty and not focused
        if (getText().isEmpty() && !isFocused && placeholder != null && !placeholder.isEmpty()) {
            g2.setColor(new Color(148, 163, 184)); // Slate-400
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 14, y);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, Math.max(size.height, 38));
    }
}
