package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernButton: A sleek, custom-painted JButton with anti-aliasing, rounded corners,
 * smooth hover transitions, and guaranteed high-contrast text across all operating systems.
 */
public class ModernButton extends JButton {

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS
    }

    private final ButtonStyle style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 10;

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Set dimensions & margins
        setMargin(new Insets(10, 18, 10, 18));

        // Hover & Press event listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int width = getWidth();
        int height = getHeight();

        Color bgColor;
        Color textColor;
        Color borderColor = null;

        switch (style) {
            case PRIMARY:
                bgColor = isPressed ? new Color(29, 78, 216) : (isHovered ? new Color(37, 99, 235) : new Color(30, 64, 175));
                textColor = Color.WHITE;
                break;
            case SECONDARY:
                bgColor = isPressed ? new Color(226, 232, 240) : (isHovered ? new Color(241, 245, 249) : Color.WHITE);
                textColor = new Color(30, 41, 59); // Slate-800
                borderColor = isHovered ? new Color(148, 163, 184) : new Color(203, 213, 225);
                break;
            case DANGER:
                bgColor = isPressed ? new Color(185, 28, 28) : (isHovered ? new Color(220, 38, 38) : new Color(239, 68, 68));
                textColor = Color.WHITE;
                break;
            case SUCCESS:
                bgColor = isPressed ? new Color(4, 120, 87) : (isHovered ? new Color(5, 150, 105) : new Color(16, 185, 129));
                textColor = Color.WHITE;
                break;
            default:
                bgColor = new Color(37, 99, 235);
                textColor = Color.WHITE;
        }

        if (!isEnabled()) {
            bgColor = new Color(226, 232, 240);
            textColor = new Color(148, 163, 184);
            borderColor = null;
        }

        // Fill background
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));

        // Draw border if applicable
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));
        }

        // Draw text centered
        g2.setColor(textColor);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
        int textX = (width - stringBounds.width) / 2;
        int textY = (height - stringBounds.height) / 2 + fm.getAscent() - 1;

        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        // Generous vertical padding for a modern, clickable feel
        return new Dimension(Math.max(size.width + 24, 100), Math.max(size.height + 14, 40));
    }
}
