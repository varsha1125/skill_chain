package com.skillchain.ui;

import com.skillchain.model.User;
import com.skillchain.service.UserService;
import com.skillchain.util.DatabaseConnection;
import com.skillchain.util.DuplicateEmailException;
import com.skillchain.util.InvalidLoginException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

/**
 * ============================================================================
 * SCREEN 1: LOGIN & REGISTRATION FRAME (ENHANCED UI/UX)
 * ============================================================================
 * Redesigned with modern visual design:
 * - Pure Java2D vector logo banner (no broken emoji glyphs)
 * - Segmented pill switch navigation
 * - Rounded input fields with interactive focus rings
 * - Custom painted high-contrast buttons with smooth hover animations
 * - Live database status pill indicator
 */
public class LoginRegisterFrame extends JFrame {

    private final UserService userService;

    // Login Form Components
    private ModernTextField txtLoginEmail;
    private ModernPasswordField txtLoginPassword;

    // Register Form Components
    private ModernTextField txtRegName;
    private ModernTextField txtRegEmail;
    private ModernPasswordField txtRegPassword;
    private ModernPasswordField txtRegConfirmPassword;

    // Navigation Switcher & Card Container
    private SegmentedSwitch tabSwitch;
    private JPanel cardContainer;
    private CardLayout cardLayout;

    // Status label
    private JLabel lblDbStatus;
    private JPanel statusPill;

    public LoginRegisterFrame() {
        this.userService = new UserService();
        initUI();
        checkDatabaseStatus();
    }

    private void initUI() {
        setTitle("Skill Chain - Student Skill Exchange Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 710);
        setMinimumSize(new Dimension(480, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252)); // Slate-50
        setLayout(new BorderLayout());

        // 1. Vector Logo & Title Header Banner
        BrandHeaderPanel headerBanner = new BrandHeaderPanel();
        add(headerBanner, BorderLayout.NORTH);

        // 2. Main Content Center Wrapper
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(new Color(248, 250, 252));
        centerWrapper.setBorder(new EmptyBorder(16, 28, 16, 28));

        // Segmented Pill Tab Switcher: [ Sign In ] [ Create Account ]
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switchPanel.setOpaque(false);
        switchPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        tabSwitch = new SegmentedSwitch(new String[]{"Sign In", "Create Account"}, index -> {
            if (index == 0) {
                cardLayout.show(cardContainer, "LOGIN");
            } else {
                cardLayout.show(cardContainer, "REGISTER");
            }
        });
        switchPanel.add(tabSwitch);
        centerWrapper.add(switchPanel, BorderLayout.NORTH);

        // Add Forms to Card Container
        cardContainer.add(createLoginCard(), "LOGIN");
        cardContainer.add(createRegisterCard(), "REGISTER");
        centerWrapper.add(cardContainer, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // 3. Footer with Status Pill Badge
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(248, 250, 252));
        footerPanel.setBorder(new EmptyBorder(8, 16, 16, 16));

        statusPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(236, 253, 245)); // Soft emerald
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));
                g2.setColor(new Color(167, 243, 208)); // Border
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14));
                g2.dispose();
            }
        };
        statusPill.setOpaque(false);
        statusPill.setBorder(new EmptyBorder(2, 10, 2, 10));

        lblDbStatus = new JLabel("Connecting to MySQL...");
        lblDbStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDbStatus.setForeground(new Color(5, 150, 105)); // Emerald
        statusPill.add(lblDbStatus);

        footerPanel.add(statusPill);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createLoginCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = row++;
        card.add(lblTitle, gbc);

        JLabel lblPrompt = new JLabel("Sign in to exchange skills with campus peers.");
        lblPrompt.setFont(UITheme.FONT_BODY);
        lblPrompt.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 6, 12, 6);
        card.add(lblPrompt, gbc);

        // Email Label & Modern Field
        gbc.insets = new Insets(4, 6, 2, 6);
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(UITheme.FONT_BODY_BOLD);
        lblEmail.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblEmail, gbc);

        txtLoginEmail = new ModernTextField("e.g. alice@university.edu");
        txtLoginEmail.setText("alice@university.edu"); // Convenience pre-fill for demo
        gbc.gridy = row++;
        gbc.insets = new Insets(2, 6, 10, 6);
        card.add(txtLoginEmail, gbc);

        // Password Label & Modern Field
        gbc.insets = new Insets(4, 6, 2, 6);
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(UITheme.FONT_BODY_BOLD);
        lblPassword.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblPassword, gbc);

        txtLoginPassword = new ModernPasswordField("Enter your password");
        txtLoginPassword.setText("Password123!"); // Convenience pre-fill for demo
        gbc.gridy = row++;
        gbc.insets = new Insets(2, 6, 16, 6);
        card.add(txtLoginPassword, gbc);

        // Primary Action Button (Guaranteed high contrast vibrant button)
        ModernButton btnLogin = new ModernButton("Sign In to Skill Chain", ModernButton.ButtonStyle.PRIMARY);
        btnLogin.addActionListener(e -> handleLogin());
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 6, 14, 6);
        card.add(btnLogin, gbc);

        // Helpful pre-seeded accounts badge
        JPanel demoBox = new JPanel();
        demoBox.setLayout(new BoxLayout(demoBox, BoxLayout.Y_AXIS));
        demoBox.setBackground(new Color(241, 245, 249)); // Slate-100
        demoBox.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblHint = new JLabel("Test Account: alice@university.edu / Password123!");
        lblHint.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHint.setForeground(new Color(71, 85, 105));
        demoBox.add(lblHint);

        JLabel lblHint2 = new JLabel("Reciprocal Partner: bob@university.edu / Password123!");
        lblHint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint2.setForeground(new Color(100, 116, 139));
        demoBox.add(lblHint2);

        gbc.gridy = row++;
        gbc.insets = new Insets(4, 6, 4, 6);
        card.add(demoBox, gbc);

        return card;
    }

    private JPanel createRegisterCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 6, 3, 6);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel lblTitle = new JLabel("Create Student Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = row++;
        card.add(lblTitle, gbc);

        JLabel lblPrompt = new JLabel("Start teaching what you know & learning what you love.");
        lblPrompt.setFont(UITheme.FONT_BODY);
        lblPrompt.setForeground(UITheme.COLOR_TEXT_MUTED);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 6, 8, 6);
        card.add(lblPrompt, gbc);

        // Full Name
        gbc.insets = new Insets(3, 6, 1, 6);
        JLabel lblName = new JLabel("Full Name");
        lblName.setFont(UITheme.FONT_BODY_BOLD);
        lblName.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblName, gbc);

        txtRegName = new ModernTextField("e.g. John Doe");
        gbc.gridy = row++;
        gbc.insets = new Insets(1, 6, 6, 6);
        card.add(txtRegName, gbc);

        // Student Email
        gbc.insets = new Insets(3, 6, 1, 6);
        JLabel lblEmail = new JLabel("Student Email");
        lblEmail.setFont(UITheme.FONT_BODY_BOLD);
        lblEmail.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblEmail, gbc);

        txtRegEmail = new ModernTextField("e.g. student@university.edu");
        gbc.gridy = row++;
        gbc.insets = new Insets(1, 6, 6, 6);
        card.add(txtRegEmail, gbc);

        // Password
        gbc.insets = new Insets(3, 6, 1, 6);
        JLabel lblPassword = new JLabel("Password (min 6 characters)");
        lblPassword.setFont(UITheme.FONT_BODY_BOLD);
        lblPassword.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblPassword, gbc);

        txtRegPassword = new ModernPasswordField("Enter strong password");
        gbc.gridy = row++;
        gbc.insets = new Insets(1, 6, 6, 6);
        card.add(txtRegPassword, gbc);

        // Confirm Password
        gbc.insets = new Insets(3, 6, 1, 6);
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(UITheme.FONT_BODY_BOLD);
        lblConfirm.setForeground(new Color(51, 65, 85));
        gbc.gridy = row++;
        card.add(lblConfirm, gbc);

        txtRegConfirmPassword = new ModernPasswordField("Re-type password");
        gbc.gridy = row++;
        gbc.insets = new Insets(1, 6, 12, 6);
        card.add(txtRegConfirmPassword, gbc);

        // Register Button (Guaranteed high-contrast text)
        ModernButton btnRegister = new ModernButton("Create Student Account", ModernButton.ButtonStyle.PRIMARY);
        btnRegister.addActionListener(e -> handleRegister());
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 6, 4, 6);
        card.add(btnRegister, gbc);

        return card;
    }

    private void handleLogin() {
        String email = txtLoginEmail.getText().trim();
        String password = new String(txtLoginPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in both your email and password.",
                    "Missing Credentials",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User authenticatedUser = userService.login(email, password);
            JOptionPane.showMessageDialog(this,
                    "Welcome back, " + authenticatedUser.getName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open Main Dashboard Frame
            MainFrame mainFrame = new MainFrame(authenticatedUser, this);
            mainFrame.setVisible(true);
            this.setVisible(false);

            txtLoginPassword.setText("");

        } catch (InvalidLoginException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error during login: " + e.getMessage() +
                    "\nPlease verify MySQL is running and config.properties has correct credentials.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String name = txtRegName.getText().trim();
        String email = txtRegEmail.getText().trim();
        String password = new String(txtRegPassword.getPassword());
        String confirmPassword = new String(txtRegConfirmPassword.getPassword());

        try {
            User createdUser = userService.register(name, email, password, confirmPassword);
            JOptionPane.showMessageDialog(this,
                    "Account registered successfully! You can now sign in with " + createdUser.getEmail(),
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear inputs
            txtRegName.setText("");
            txtRegEmail.setText("");
            txtRegPassword.setText("");
            txtRegConfirmPassword.setText("");

            // Switch to Login Tab
            tabSwitch.setSelectedIndex(0);
            txtLoginEmail.setText(createdUser.getEmail());
            txtLoginPassword.setText(password);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DuplicateEmailException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Account Already Exists",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error during registration: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkDatabaseStatus() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.testConnection();
            }

            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        lblDbStatus.setText("● MySQL Connected: skill_chain_db");
                        lblDbStatus.setForeground(new Color(5, 150, 105)); // Emerald
                    } else {
                        lblDbStatus.setText("⚠ MySQL Disconnected (Check config.properties)");
                        lblDbStatus.setForeground(new Color(220, 38, 38)); // Rose
                    }
                } catch (Exception e) {
                    lblDbStatus.setText("⚠ Connection Error: " + e.getMessage());
                    lblDbStatus.setForeground(new Color(220, 38, 38));
                }
            }
        };
        worker.execute();
    }
}
