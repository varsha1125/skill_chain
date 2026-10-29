package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** Compact rectangular tab switch with an animated active-block slide. */
public class SegmentedSwitch extends JPanel {

    private final String[] options;
    private final Consumer<Integer> onSelectionChange;
    private int selectedIndex;
    private float activePosition;
    private Timer slideTimer;

    public SegmentedSwitch(String[] options, Consumer<Integer> onSelectionChange) {
        this.options = options;
        this.onSelectionChange = onSelectionChange;
        this.activePosition = 0;
        setOpaque(false);
        setPreferredSize(new Dimension(340, 48));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int segmentWidth = Math.max(1, getWidth() / options.length);
                int clickedIndex = Math.min(e.getX() / segmentWidth, options.length - 1);
                setSelectedIndex(clickedIndex);
            }
        });
    }

    public void setSelectedIndex(int index) {
        if (index < 0 || index >= options.length || index == selectedIndex) {
            return;
        }
        selectedIndex = index;
        if (slideTimer != null && slideTimer.isRunning()) {
            slideTimer.stop();
        }
        slideTimer = new Timer(14, e -> {
            float delta = selectedIndex - activePosition;
            activePosition += delta * 0.28f;
            if (Math.abs(delta) < 0.02f) {
                activePosition = selectedIndex;
                slideTimer.stop();
            }
            repaint();
        });
        slideTimer.start();
        if (onSelectionChange != null) {
            onSelectionChange.accept(selectedIndex);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        int segmentWidth = Math.max(1, w / options.length);

        g2.setColor(UITheme.COLOR_INK);
        g2.fillRect(4, 5, Math.max(0, w - 4), Math.max(0, h - 5));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, Math.max(0, w - 5), Math.max(0, h - 5));
        g2.setColor(UITheme.COLOR_INK);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRect(1, 1, Math.max(0, w - 7), Math.max(0, h - 7));

        int activeX = Math.round(activePosition * segmentWidth) + 4;
        g2.setColor(UITheme.COLOR_PRIMARY);
        g2.fillRect(activeX, 5, Math.max(0, segmentWidth - 8), Math.max(0, h - 13));
        g2.setColor(UITheme.COLOR_INK);
        g2.drawRect(activeX, 5, Math.max(0, segmentWidth - 8), Math.max(0, h - 13));

        g2.setFont(UITheme.FONT_BODY_BOLD);
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < options.length; i++) {
            int x = i * segmentWidth;
            String text = options[i];
            g2.setColor(i == selectedIndex ? UITheme.COLOR_INK : UITheme.COLOR_TEXT_MUTED);
            g2.drawString(text, x + (segmentWidth - fm.stringWidth(text)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
        }
        g2.dispose();
    }
}
