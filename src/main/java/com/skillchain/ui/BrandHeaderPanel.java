package com.skillchain.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * BrandHeaderPanel: A premium banner rendering a custom-drawn geometric chain link logo,
 * rich gradient background, and crisp typography without relying on emoji fonts.
 */
public class BrandHeaderPanel extends JPanel {

    public BrandHeaderPanel() {
        setPreferredSize(new Dimension(500, 110));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        // Elegant Gradient Background: Deep Slate/Navy to Vibrant Indigo Blue
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(15, 23, 42),       // Slate-900
                w, h, new Color(30, 58, 138)       // Blue-900
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // Subtle accent line at the bottom
        GradientPaint accentLine = new GradientPaint(
                0, h - 3, new Color(37, 99, 235),  // Primary Blue
                w, h - 3, new Color(16, 185, 129)  // Emerald
        );
        g2.setPaint(accentLine);
        g2.fillRect(0, h - 3, w, 3);

        // Draw Interlocking Chain Logo Icon (Pure Java2D Vector Art)
        int logoSize = 44;
        int logoX = 28;
        int logoY = (h - logoSize) / 2 - 2;

        // Outer glow circle
        g2.setColor(new Color(37, 99, 235, 60));
        g2.fillOval(logoX - 6, logoY - 6, logoSize + 12, logoSize + 12);

        // Logo background circle
        g2.setColor(new Color(30, 64, 175));
        g2.fillOval(logoX, logoY, logoSize, logoSize);
        g2.setColor(new Color(96, 165, 250));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(logoX, logoY, logoSize, logoSize);

        // Interlocking links (Vector drawing)
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Link 1 (Emerald/Cyan)
        g2.setColor(new Color(52, 211, 153));
        g2.draw(new RoundRectangle2D.Float(logoX + 10, logoY + 12, 16, 10, 6, 6));

        // Link 2 (White/Gold)
        g2.setColor(Color.WHITE);
        g2.draw(new RoundRectangle2D.Float(logoX + 18, logoY + 20, 16, 10, 6, 6));

        // Typography: Title & Subtitle
        int textX = logoX + logoSize + 16;

        // Title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        g2.drawString("SKILL CHAIN", textX, logoY + 22);

        // Tag badge next to title
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(16, 185, 129));
        g2.drawString("STUDENT EXCHANGE", textX + 155, logoY + 12);

        // Subtitle
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(191, 219, 254)); // Light Blue
        g2.drawString("Peer-to-Peer Reciprocal Knowledge & Skill Network", textX, logoY + 40);

        g2.dispose();
    }
}
