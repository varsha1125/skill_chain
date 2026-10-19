package com.skillchain;

import com.skillchain.ui.LoginRegisterFrame;

import javax.swing.*;

/**
 * ============================================================================
 * SKILL CHAIN - A SKILL EXCHANGE PLATFORM FOR STUDENTS
 * Application Entry Point (Main.java)
 * ============================================================================
 */
public class Main {

    public static void main(String[] args) {
        // Set native system look-and-feel for clean UI integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Launch Desktop Swing Application on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginRegisterFrame loginFrame = new LoginRegisterFrame();
            loginFrame.setVisible(true);
        });
    }
}
