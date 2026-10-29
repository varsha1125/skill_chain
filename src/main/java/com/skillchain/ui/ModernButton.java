package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Neo-brutalist action button with a hard offset shadow and tactile press state.
 */
public class ModernButton extends JButton {

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS
    }

    private final ButtonStyle style;
    private boolean hovered;
    private boolean pressed;

    public ModernButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(UITheme.FONT_BODY_BOLD);
        setMargin(new Insets(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public ModernButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int width = getWidth();
        int height = getHeight();
        int offset = pressed ? 2 : 5;

        Color background;
        Color textColor = UITheme.COLOR_INK;
        switch (style) {
            case PRIMARY -> background = hovered ? UITheme.COLOR_PRIMARY_DARK : UITheme.COLOR_PRIMARY;
            case SECONDARY -> background = hovered ? UITheme.COLOR_SECONDARY : Color.WHITE;
            case DANGER -> background = hovered ? new Color(239, 84, 84) : UITheme.COLOR_DANGER;
            case SUCCESS -> background = hovered ? new Color(65, 190, 105) : UITheme.COLOR_SUCCESS;
            default -> background = UITheme.COLOR_PRIMARY;
        }
        if (!isEnabled()) {
            background = new Color(220, 220, 220);
            textColor = new Color(120, 120, 120);
        }

        g2.setColor(UITheme.COLOR_INK);
        g2.fillRect(offset, offset, Math.max(0, width - offset), Math.max(0, height - offset));
        g2.setColor(background);
        g2.fillRect(0, 0, Math.max(0, width - offset - 1), Math.max(0, height - offset - 1));
        g2.setColor(UITheme.COLOR_INK);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRect(1, 1, Math.max(0, width - offset - 3), Math.max(0, height - offset - 3));

        g2.setColor(textColor);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent() - (pressed ? 1 : 2);
        g2.drawString(getText(), textX, textY);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(Math.max(size.width + 22, 108), Math.max(size.height + 16, 44));
    }
}
