package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;

/**
 * Flat, animated brand strip. The moving cyan marker is deliberately subtle:
 * it adds life without turning the login screen into a loading animation.
 */
public class BrandHeaderPanel extends JPanel {

    private int markerOffset;
    private final Timer markerTimer;

    public BrandHeaderPanel() {
        setPreferredSize(new Dimension(500, 132));
        setOpaque(false);
        markerTimer = new Timer(45, e -> {
            markerOffset = (markerOffset + 3) % 180;
            repaint();
        });
        markerTimer.start();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !isDisplayable()) {
                markerTimer.stop();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        g2.setColor(UITheme.COLOR_INK);
        g2.fillRect(0, 0, w, h);

        // Graphic blocks establish the neo-brutalist visual rhythm.
        g2.setColor(UITheme.COLOR_PRIMARY);
        g2.fillRect(0, 0, Math.min(w, 18), h);
        g2.fillRect(Math.max(0, w - 86), 0, Math.min(86, w), 12);
        g2.setColor(UITheme.COLOR_ACCENT);
        g2.fillRect(Math.max(0, w - 150), h - 12, Math.min(150, w), 12);
        g2.setColor(UITheme.COLOR_SECONDARY);
        g2.fillRect(30 + markerOffset, 14, 34, 5);

        int logoSize = 58;
        int logoX = 34;
        int logoY = (h - logoSize) / 2;
        g2.setColor(UITheme.COLOR_PRIMARY);
        g2.fillRect(logoX, logoY, logoSize, logoSize);
        g2.setColor(UITheme.COLOR_INK);
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawRect(logoX, logoY, logoSize, logoSize);
        g2.drawRoundRect(logoX + 12, logoY + 17, 24, 15, 8, 8);
        g2.setColor(UITheme.COLOR_SECONDARY);
        g2.drawRoundRect(logoX + 22, logoY + 26, 24, 15, 8, 8);

        int textX = logoX + logoSize + 22;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 25));
        g2.drawString("SKILL CHAIN", textX, logoY + 26);
        g2.setColor(UITheme.COLOR_PRIMARY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("CAMPUS SWAP / V1", textX, logoY + 46);
        g2.setColor(new Color(230, 230, 230));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Teach what you know. Find what you need.", textX, logoY + 64);

        g2.dispose();
    }
}
