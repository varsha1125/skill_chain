package com.skillchain.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Rectangular password field matching the shared neo-brutalist input style. */
public class ModernPasswordField extends JPasswordField {

    private boolean focused;
    private String placeholder = "";

    public ModernPasswordField() {
        init();
    }

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(UITheme.FONT_BODY);
        setForeground(UITheme.COLOR_TEXT_PRIMARY);
        setCaretColor(UITheme.COLOR_TEXT_PRIMARY);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
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
        int width = getWidth();
        int height = getHeight();
        g2.setColor(focused ? UITheme.COLOR_SECONDARY : Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(UITheme.COLOR_INK);
        g2.setStroke(new BasicStroke(focused ? 3.0f : 2.0f));
        g2.drawRect(1, 1, Math.max(0, width - 3), Math.max(0, height - 3));
        super.paintComponent(g);
        if (getPassword().length == 0 && !focused && placeholder != null && !placeholder.isEmpty()) {
            g2.setColor(UITheme.COLOR_TEXT_MUTED);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(placeholder, 13, (height - fm.getHeight()) / 2 + fm.getAscent());
        }
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, Math.max(size.height, 42));
    }
}
