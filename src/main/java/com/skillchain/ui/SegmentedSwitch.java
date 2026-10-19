package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * SegmentedSwitch: A modern pill-shaped toggle switch replacing dated Swing JTabbedPanes.
 */
public class SegmentedSwitch extends JPanel {

    private final String[] options;
    private int selectedIndex = 0;
    private Consumer<Integer> onSelectionChange;

    public SegmentedSwitch(String[] options, Consumer<Integer> onSelectionChange) {
        this.options = options;
        this.onSelectionChange = onSelectionChange;
        setOpaque(false);
        setPreferredSize(new Dimension(320, 42));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int width = getWidth();
                int segmentWidth = width / options.length;
                int clickedIndex = Math.min(e.getX() / segmentWidth, options.length - 1);
                if (clickedIndex != selectedIndex) {
                    selectedIndex = clickedIndex;
                    repaint();
                    if (onSelectionChange != null) {
                        onSelectionChange.accept(selectedIndex);
                    }
                }
            }
        });
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.length && index != selectedIndex) {
            this.selectedIndex = index;
            repaint();
            if (onSelectionChange != null) {
                onSelectionChange.accept(selectedIndex);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        int padding = 4;
        int radius = 12;

        // Container Pill: Soft Slate-200 background
        g2.setColor(new Color(241, 245, 249)); // Slate-100
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius + 4, radius + 4));

        g2.setColor(new Color(226, 232, 240)); // Slate-200
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius + 4, radius + 4));

        // Active Segment Pill
        int segmentWidth = (w - (padding * 2)) / options.length;
        int activeX = padding + (selectedIndex * segmentWidth);
        int activeY = padding;
        int activeW = segmentWidth;
        int activeH = h - (padding * 2);

        // Active pill shadow & background
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fill(new RoundRectangle2D.Float(activeX, activeY + 1, activeW, activeH, radius, radius));

        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(activeX, activeY, activeW, activeH, radius, radius));

        g2.setColor(new Color(203, 213, 225));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(activeX, activeY, activeW, activeH, radius, radius));

        // Draw Options Labels
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < options.length; i++) {
            int segX = padding + (i * segmentWidth);
            boolean isSelected = (i == selectedIndex);

            g2.setColor(isSelected ? new Color(15, 23, 42) : new Color(100, 116, 139));
            String text = options[i];
            int textW = fm.stringWidth(text);
            int textX = segX + (segmentWidth - textW) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(text, textX, textY);
        }

        g2.dispose();
    }
}
