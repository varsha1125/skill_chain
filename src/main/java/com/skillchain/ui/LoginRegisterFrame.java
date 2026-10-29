package com.skillchain.ui;

import com.skillchain.model.User;
import com.skillchain.service.UserService;
import com.skillchain.util.DatabaseConnection;
import com.skillchain.util.DatabaseException;
import com.skillchain.util.DuplicateEmailException;
import com.skillchain.util.InvalidLoginException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Login and registration screen for Skill Chain.
 *
 * The screen is deliberately compact and graphic: the form deck is anchored
 * to its preferred height, so resizing the window does not create a giant
 * empty white area. Database setup is available inside the app rather than
 * being exposed as a confusing raw exception dialog.
 */
public class LoginRegisterFrame extends JFrame {

    private final UserService userService;
    private ModernTextField txtLoginEmail;
    private ModernPasswordField txtLoginPassword;
    private ModernTextField txtRegName;
    private ModernTextField txtRegEmail;
    private ModernPasswordField txtRegPassword;
    private ModernPasswordField txtRegConfirmPassword;
    private SegmentedSwitch tabSwitch;
    private JPanel cardContainer;
    private CardLayout cardLayout;
    private JLabel lblDbStatus;
    private JPanel statusPill;
    private ModernButton btnLoginAction;
    private ModernButton btnRegisterAction;

    public LoginRegisterFrame() {
        userService = new UserService();
        initUI();
        checkDatabaseStatus();
    }

    private void initUI() {
        setTitle("Skill Chain / Campus Skill Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 760);
        setMinimumSize(new Dimension(560, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.COLOR_BG);
        setLayout(new BorderLayout());

        add(new BrandHeaderPanel(), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(UITheme.COLOR_BG);
        centerWrapper.setBorder(new EmptyBorder(18, 34, 12, 34));

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switchPanel.setOpaque(false);
        switchPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);
        cardContainer.add(createLoginCard(), "LOGIN");
        cardContainer.add(createRegisterCard(), "REGISTER");

        tabSwitch = new SegmentedSwitch(new String[]{"SIGN IN", "CREATE ACCOUNT"}, index -> {
            cardLayout.show(cardContainer, index == 0 ? "LOGIN" : "REGISTER");
        });
        switchPanel.add(tabSwitch);
        centerWrapper.add(switchPanel, BorderLayout.NORTH);

        // Anchor the form deck to its preferred height instead of stretching it.
        JPanel formDeck = new JPanel(new BorderLayout());
        formDeck.setOpaque(false);
        formDeck.add(cardContainer, BorderLayout.NORTH);
        centerWrapper.add(formDeck, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout(12, 0));
        footerPanel.setBackground(UITheme.COLOR_BG);
        footerPanel.setBorder(new EmptyBorder(8, 34, 18, 34));

        statusPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        statusPill.setOpaque(true);
        statusPill.setBackground(UITheme.COLOR_PRIMARY);
        statusPill.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_INK, 2));
        lblDbStatus = new JLabel("CHECKING DATABASE...");
        lblDbStatus.setFont(UITheme.FONT_BODY_BOLD);
        lblDbStatus.setForeground(UITheme.COLOR_INK);
        statusPill.add(lblDbStatus);

        ModernButton btnSetup = new ModernButton("CONFIGURE DATABASE", ModernButton.ButtonStyle.SECONDARY);
        btnSetup.setToolTipText("Enter your local MySQL connection settings");
        btnSetup.addActionListener(e -> openDatabaseSetupDialog());
        footerPanel.add(statusPill, BorderLayout.CENTER);
        footerPanel.add(btnSetup, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createLoginCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = formConstraints();
        int row = 0;

        JLabel title = label("WELCOME BACK", UITheme.FONT_TITLE, UITheme.COLOR_TEXT_PRIMARY);
        gbc.gridy = row++;
        card.add(title, gbc);
        JLabel prompt = label("Sign in and find your next skill exchange.", UITheme.FONT_BODY, UITheme.COLOR_TEXT_MUTED);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 6, 16, 6);
        card.add(prompt, gbc);

        gbc.insets = new Insets(4, 6, 2, 6);
        card.add(label("EMAIL ADDRESS", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtLoginEmail = new ModernTextField("e.g. alice@university.edu");
        txtLoginEmail.setText("alice@university.edu");
        gbc.insets = new Insets(2, 6, 12, 6);
        card.add(txtLoginEmail, at(gbc, row++));

        gbc.insets = new Insets(4, 6, 2, 6);
        card.add(label("PASSWORD", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtLoginPassword = new ModernPasswordField("Enter your password");
        txtLoginPassword.setText("Password123!");
        gbc.insets = new Insets(2, 6, 16, 6);
        card.add(txtLoginPassword, at(gbc, row++));

        btnLoginAction = new ModernButton("SIGN IN TO SKILL CHAIN", ModernButton.ButtonStyle.PRIMARY);
        btnLoginAction.addActionListener(e -> handleLogin());
        gbc.insets = new Insets(6, 6, 14, 6);
        card.add(btnLoginAction, at(gbc, row++));

        JPanel demoBox = new JPanel();
        demoBox.setLayout(new BoxLayout(demoBox, BoxLayout.Y_AXIS));
        demoBox.setBackground(UITheme.COLOR_SECONDARY);
        demoBox.setBorder(new CompoundBorderCompat(UITheme.COLOR_INK, 2, 8, 10, 8, 10));
        demoBox.add(label("DEMO ACCOUNT", UITheme.FONT_BODY_BOLD, UITheme.COLOR_INK));
        demoBox.add(label("alice@university.edu / Password123!", UITheme.FONT_SMALL, UITheme.COLOR_INK));
        demoBox.add(label("Try Bob after sending a swap request.", UITheme.FONT_SMALL, UITheme.COLOR_INK));
        card.add(demoBox, at(gbc, row));
        return card;
    }

    private JPanel createRegisterCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = formConstraints();
        int row = 0;

        card.add(label("CREATE STUDENT ACCOUNT", UITheme.FONT_TITLE, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        gbc.insets = new Insets(0, 6, 10, 6);
        card.add(label("Build your teaching and learning portfolio.", UITheme.FONT_BODY, UITheme.COLOR_TEXT_MUTED), at(gbc, row++));

        gbc.insets = new Insets(3, 6, 2, 6);
        card.add(label("FULL NAME", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtRegName = new ModernTextField("e.g. John Doe");
        gbc.insets = new Insets(1, 6, 7, 6);
        card.add(txtRegName, at(gbc, row++));

        gbc.insets = new Insets(3, 6, 2, 6);
        card.add(label("STUDENT EMAIL", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtRegEmail = new ModernTextField("e.g. student@university.edu");
        gbc.insets = new Insets(1, 6, 7, 6);
        card.add(txtRegEmail, at(gbc, row++));

        gbc.insets = new Insets(3, 6, 2, 6);
        card.add(label("PASSWORD / MIN 6 CHARACTERS", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtRegPassword = new ModernPasswordField("Enter a password");
        gbc.insets = new Insets(1, 6, 7, 6);
        card.add(txtRegPassword, at(gbc, row++));

        gbc.insets = new Insets(3, 6, 2, 6);
        card.add(label("CONFIRM PASSWORD", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        txtRegConfirmPassword = new ModernPasswordField("Re-type password");
        gbc.insets = new Insets(1, 6, 14, 6);
        card.add(txtRegConfirmPassword, at(gbc, row++));

        btnRegisterAction = new ModernButton("CREATE STUDENT ACCOUNT", ModernButton.ButtonStyle.PRIMARY);
        btnRegisterAction.addActionListener(e -> handleRegister());
        gbc.insets = new Insets(4, 6, 4, 6);
        card.add(btnRegisterAction, at(gbc, row));
        return card;
    }

    private GridBagConstraints formConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 6, 5, 6);
        return gbc;
    }

    private GridBagConstraints at(GridBagConstraints source, int row) {
        source.gridy = row;
        return source;
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void handleLogin() {
        String email = txtLoginEmail.getText().trim();
        String password = new String(txtLoginPassword.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in both your email and password.", "Missing Credentials", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User authenticatedUser = userService.login(email, password);
            showMessage("Welcome back, " + authenticatedUser.getName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            MainFrame mainFrame = new MainFrame(authenticatedUser, this);
            mainFrame.setVisible(true);
            setVisible(false);
            txtLoginPassword.setText("");
        } catch (InvalidLoginException e) {
            showMessage(e.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | DatabaseException e) {
            showDatabaseProblem(e);
        }
    }

    private void handleRegister() {
        String name = txtRegName.getText().trim();
        String email = txtRegEmail.getText().trim();
        String password = new String(txtRegPassword.getPassword());
        String confirmPassword = new String(txtRegConfirmPassword.getPassword());
        try {
            User createdUser = userService.register(name, email, password, confirmPassword);
            showMessage("Account registered successfully. You can now sign in with " + createdUser.getEmail(), "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            txtRegName.setText("");
            txtRegEmail.setText("");
            txtRegPassword.setText("");
            txtRegConfirmPassword.setText("");
            tabSwitch.setSelectedIndex(0);
            txtLoginEmail.setText(createdUser.getEmail());
            txtLoginPassword.setText(password);
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (DuplicateEmailException e) {
            showMessage(e.getMessage(), "Account Already Exists", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | DatabaseException e) {
            showDatabaseProblem(e);
        }
    }

    private void checkDatabaseStatus() {
        SwingWorker<DatabaseState, Void> worker = new SwingWorker<>() {
            @Override
            protected DatabaseState doInBackground() {
                String issue = DatabaseConnection.getConfigurationIssue();
                return new DatabaseState(issue, issue == null && DatabaseConnection.testConnection());
            }

            @Override
            protected void done() {
                try {
                    DatabaseState state = get();
                    if (state.connected) {
                        setDatabaseStatus("MYSQL CONNECTED / SKILL_CHAIN_DB", UITheme.COLOR_SUCCESS);
                    } else if (state.configurationIssue != null) {
                        setDatabaseStatus("SETUP NEEDED / USE CONFIGURE DATABASE", UITheme.COLOR_PRIMARY);
                    } else {
                        setDatabaseStatus("MYSQL OFFLINE / START YOUR SERVER", UITheme.COLOR_DANGER);
                    }
                    setAuthEnabled(state.connected);
                } catch (Exception e) {
                    setDatabaseStatus("DATABASE CHECK FAILED / USE CONFIGURE DATABASE", UITheme.COLOR_DANGER);
                    setAuthEnabled(false);
                }
            }
        };
        worker.execute();
    }

    private void setDatabaseStatus(String text, Color color) {
        lblDbStatus.setText(text);
        lblDbStatus.setForeground(UITheme.COLOR_INK);
        statusPill.setBackground(color);
        statusPill.repaint();
    }

    private void setAuthEnabled(boolean enabled) {
        if (btnLoginAction != null) {
            btnLoginAction.setEnabled(enabled);
            btnLoginAction.setToolTipText(enabled ? null : "Configure a working MySQL connection first");
        }
        if (btnRegisterAction != null) {
            btnRegisterAction.setEnabled(enabled);
            btnRegisterAction.setToolTipText(enabled ? null : "Configure a working MySQL connection first");
        }
    }

    private void showDatabaseProblem(Exception exception) {
        String configurationIssue = DatabaseConnection.getConfigurationIssue();
        String message;
        if (configurationIssue != null) {
            message = "Database setup is incomplete.\n\nUse CONFIGURE DATABASE below to save your local MySQL URL, username, and password.\n\n" + configurationIssue;
        } else {
            message = "Skill Chain cannot reach MySQL.\n\nStart MySQL and confirm that the database created by schema.sql is available.\n\nTechnical detail: " + exception.getMessage();
        }
        Object[] options = {"CONFIGURE DATABASE", "CLOSE"};
        int choice = JOptionPane.showOptionDialog(this, message, "Database Setup", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            openDatabaseSetupDialog();
        }
    }

    private void openDatabaseSetupDialog() {
        JDialog dialog = new JDialog(this, "Configure MySQL", true);
        dialog.setSize(520, 390);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(UITheme.COLOR_BG);
        content.setBorder(new EmptyBorder(22, 24, 12, 24));
        GridBagConstraints gbc = formConstraints();
        int row = 0;
        content.add(label("DATABASE SETUP", UITheme.FONT_TITLE, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        gbc.insets = new Insets(4, 6, 12, 6);
        content.add(label("These settings are saved only to your local ignored config.properties.", UITheme.FONT_SMALL, UITheme.COLOR_TEXT_MUTED), at(gbc, row++));
        gbc.insets = new Insets(4, 6, 2, 6);
        content.add(label("JDBC URL", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        ModernTextField urlField = new ModernTextField("jdbc:mysql://localhost:3306/skill_chain_db");
        urlField.setText("jdbc:mysql://localhost:3306/skill_chain_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        gbc.insets = new Insets(2, 6, 8, 6);
        content.add(urlField, at(gbc, row++));
        gbc.insets = new Insets(4, 6, 2, 6);
        content.add(label("MYSQL USER", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        ModernTextField userField = new ModernTextField("root");
        userField.setText("root");
        gbc.insets = new Insets(2, 6, 8, 6);
        content.add(userField, at(gbc, row++));
        gbc.insets = new Insets(4, 6, 2, 6);
        content.add(label("MYSQL PASSWORD", UITheme.FONT_BODY_BOLD, UITheme.COLOR_TEXT_PRIMARY), at(gbc, row++));
        ModernPasswordField passwordField = new ModernPasswordField("Enter your local MySQL password");
        gbc.insets = new Insets(2, 6, 4, 6);
        content.add(passwordField, at(gbc, row));
        dialog.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(UITheme.COLOR_BG);
        ModernButton cancel = new ModernButton("CANCEL", ModernButton.ButtonStyle.SECONDARY);
        cancel.addActionListener(e -> dialog.dispose());
        ModernButton save = new ModernButton("SAVE & TEST", ModernButton.ButtonStyle.PRIMARY);
        save.addActionListener(e -> {
            try {
                DatabaseConnection.saveConfiguration(urlField.getText(), userField.getText(), new String(passwordField.getPassword()));
                dialog.dispose();
                checkDatabaseStatus();
            } catch (DatabaseException ex) {
                showMessage(ex.getMessage(), "Could Not Save Setup", JOptionPane.ERROR_MESSAGE);
            }
        });
        actions.add(cancel);
        actions.add(save);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private static final class DatabaseState {
        private final String configurationIssue;
        private final boolean connected;

        private DatabaseState(String configurationIssue, boolean connected) {
            this.configurationIssue = configurationIssue;
            this.connected = connected;
        }
    }

    /** Small border helper keeps the card code readable without rounded pills. */
    private static final class CompoundBorderCompat extends javax.swing.border.CompoundBorder {
        private CompoundBorderCompat(Color color, int thickness, int top, int left, int bottom, int right) {
            super(new javax.swing.border.LineBorder(color, thickness), new EmptyBorder(top, left, bottom, right));
        }
    }
}
