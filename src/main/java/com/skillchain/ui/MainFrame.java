package com.skillchain.ui;

import com.skillchain.model.ActivePartnership;
import com.skillchain.model.AppNotification;
import com.skillchain.model.LearningRequest;
import com.skillchain.model.PeerStudent;
import com.skillchain.model.ReciprocalMatch;
import com.skillchain.model.Skill;
import com.skillchain.model.SkillType;
import com.skillchain.model.User;
import com.skillchain.model.UserSkill;
import com.skillchain.service.MatchingService;
import com.skillchain.service.RequestService;
import com.skillchain.service.SkillService;
import com.skillchain.util.DuplicateSkillException;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * MAIN APPLICATION FRAME (EMAIL DIRECTORY WITH DIRECT REQUEST BUTTONS)
 * ============================================================================
 * Features:
 * - Direct "Send Request ✉" button placed immediately next to each student's email!
 * - Dedicated "Send Request (Browse Student Emails)" tab in Learning Requests & Directory
 * - Symbol-only Notification Bell (🔔) with unread badge and Notification Center
 * - Active Learning Status Tracker for ongoing accepted partnerships
 * - Smart Dual-Conditional Reciprocal Matching Engine
 */
public class MainFrame extends JFrame {

    private final User currentUser;
    private final JFrame loginFrame;
    private final SkillService skillService;
    private final MatchingService matchingService;
    private final RequestService requestService;

    // Main Tabbed Navigation
    private JTabbedPane tabbedPane;
    private JTabbedPane requestsSubTabs;

    // Header Notification Button (Symbol Only)
    private JButton btnNotifications;
    private int notificationCount = 0;
    private List<AppNotification> cachedNotifications = new ArrayList<>();

    // UI Components for "My Skills"
    private JTable tblMySkills;
    private DefaultTableModel skillsTableModel;
    private JLabel lblTeachCount;
    private JLabel lblLearnCount;
    private JComboBox<String> cmbFilterType;
    private ModernTextField txtSearchSkill;

    // Dashboard Metric Labels & Active Partnerships
    private JLabel lblDashTeach;
    private JLabel lblDashLearn;
    private JLabel lblDashMatches;
    private JLabel lblDashRequests;
    private JPanel activePartnershipsContainer;

    // Student Cards Containers (Displays email with direct "Send Request" button)
    private JPanel peerCardsContainer;
    private JPanel requestsPeerCardsContainer;
    private ModernTextField txtSearchPeers;
    private List<PeerStudent> cachedPeerStudents = new ArrayList<>();

    // Reciprocal Matches UI
    private JTable tblMatches;
    private DefaultTableModel matchesTableModel;
    private JLabel lblMatchesCount;
    private ModernTextField txtSearchMatches;
    private List<ReciprocalMatch> cachedMatches = new ArrayList<>();

    // Requests UI
    private JTable tblIncomingRequests;
    private DefaultTableModel incomingTableModel;
    private JTable tblOutgoingRequests;
    private DefaultTableModel outgoingTableModel;
    private JLabel lblIncomingCount;
    private JLabel lblOutgoingCount;

    public MainFrame(User user, JFrame loginFrame) {
        this.currentUser = user;
        this.loginFrame = loginFrame;
        this.skillService = new SkillService();
        this.matchingService = new MatchingService();
        this.requestService = new RequestService();

        initUI();
        refreshAllData();
    }

    private void initUI() {
        setTitle("Skill Chain - Student Dashboard [" + currentUser.getName() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1140, 780);
        setMinimumSize(new Dimension(980, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout());

        // 1. Top Modern Navigation Header with Symbol-Only Bell & Profile
        add(createTopHeader(), BorderLayout.NORTH);

        // 2. Tabbed Navigation across the Core Screens
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Screen 1: Dashboard
        tabbedPane.addTab("  Dashboard  ", createDashboardPanel());

        // Screen 2: My Skills
        tabbedPane.addTab("  My Skills  ", createMySkillsPanel());

        // Screen 3: Campus Directory & Reciprocal Matches
        tabbedPane.addTab("  Campus Directory & Matches  ", createDirectoryAndMatchesPanel());

        // Screen 4: Learning Requests (contains Send Request by Email tab)
        tabbedPane.addTab("  Learning Requests  ", createRequestsPanel());

        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                refreshDashboardStats();
                loadActivePartnerships();
            } else if (selected == 1) {
                loadUserSkillsData();
            } else if (selected == 2) {
                loadPeerDirectory();
                loadReciprocalMatches();
            } else if (selected == 3) {
                loadPeerDirectory();
                loadRequestsData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 24, 12, 24)
        ));

        // Left: Vector Logo Badge & Platform Identity
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JPanel brandIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, 36, 36, 10, 10));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(8, 10, 12, 8, 4, 4));
                g2.draw(new RoundRectangle2D.Float(16, 16, 12, 8, 4, 4));
                g2.dispose();
            }
        };
        brandIcon.setPreferredSize(new Dimension(36, 36));
        brandIcon.setOpaque(false);
        leftPanel.add(brandIcon);

        JPanel brandTextPanel = new JPanel();
        brandTextPanel.setLayout(new BoxLayout(brandTextPanel, BoxLayout.Y_AXIS));
        brandTextPanel.setOpaque(false);

        JLabel lblBrand = new JLabel("SKILL CHAIN");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrand.setForeground(new Color(15, 23, 42));

        JLabel lblTag = new JLabel("Campus Student Exchange");
        lblTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTag.setForeground(new Color(100, 116, 139));

        brandTextPanel.add(lblBrand);
        brandTextPanel.add(lblTag);
        leftPanel.add(brandTextPanel);

        // Right: Notification Bell (Symbol Only), Student Profile Avatar & Sign Out
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);

        // Notification Bell Icon Button (Symbol Only)
        btnNotifications = new JButton() {
            boolean isHovered = false;

            {
                setPreferredSize(new Dimension(38, 38));
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Notifications");

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Circular background
                if (isHovered) {
                    g2.setColor(new Color(239, 246, 255));
                    g2.fillOval(0, 0, w, h);
                    g2.setColor(new Color(191, 219, 254));
                    g2.drawOval(0, 0, w - 1, h - 1);
                } else {
                    g2.setColor(new Color(248, 250, 252));
                    g2.fillOval(0, 0, w, h);
                    g2.setColor(new Color(226, 232, 240));
                    g2.drawOval(0, 0, w - 1, h - 1);
                }

                // Bell Icon Symbol Only
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                String bell = "🔔";
                int bx = (w - fm.stringWidth(bell)) / 2;
                int by = (h - fm.getHeight()) / 2 + fm.getAscent() - 1;
                g2.drawString(bell, bx, by);

                // Notification counter badge if > 0
                if (notificationCount > 0) {
                    int badgeSize = 14;
                    int badgeX = w - badgeSize - 1;
                    int badgeY = 1;
                    g2.setColor(new Color(239, 68, 68));
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    FontMetrics bfm = g2.getFontMetrics();
                    String countStr = String.valueOf(Math.min(notificationCount, 9));
                    int tx = badgeX + (badgeSize - bfm.stringWidth(countStr)) / 2;
                    int ty = badgeY + (badgeSize - bfm.getHeight()) / 2 + bfm.getAscent();
                    g2.drawString(countStr, tx, ty);
                }

                g2.dispose();
            }
        };
        btnNotifications.addActionListener(e -> showNotificationsDialog());
        rightPanel.add(btnNotifications);

        // Avatar Chip
        String initials = getInitials(currentUser.getName());
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 246, 255));
                g2.fillOval(0, 0, 38, 38);
                g2.setColor(new Color(37, 99, 235));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, 37, 37);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (38 - fm.stringWidth(initials)) / 2;
                int y = (38 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(38, 38));
        avatarCircle.setOpaque(false);

        JPanel userDetail = new JPanel();
        userDetail.setLayout(new BoxLayout(userDetail, BoxLayout.Y_AXIS));
        userDetail.setOpaque(false);

        JLabel lblName = new JLabel(currentUser.getName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(15, 23, 42));

        JLabel lblEmail = new JLabel(currentUser.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(new Color(100, 116, 139));

        userDetail.add(lblName);
        userDetail.add(lblEmail);

        rightPanel.add(avatarCircle);
        rightPanel.add(userDetail);

        ModernButton btnLogout = new ModernButton("Sign Out", ModernButton.ButtonStyle.SECONDARY);
        btnLogout.addActionListener(e -> handleLogout());
        rightPanel.add(btnLogout);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    // ========================================================================
    // SCREEN 1: DASHBOARD
    // ========================================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 26, 22, 26));

        // 1. Sleek Gradient Hero Banner
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        w, h, new Color(30, 58, 138)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(w - 180, -40, 220, 220);
                g2.fillOval(w - 280, 40, 120, 120);
                g2.dispose();
            }
        };
        hero.setLayout(new BorderLayout());
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 26, 22, 26));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel heroLeft = new JPanel();
        heroLeft.setLayout(new BoxLayout(heroLeft, BoxLayout.Y_AXIS));
        heroLeft.setOpaque(false);

        JLabel lblTag = new JLabel("● CAMPUS PEER NETWORK  •  STUDENT PORTAL");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTag.setForeground(new Color(52, 211, 153));

        JLabel lblGreeting = new JLabel("Welcome back, " + currentUser.getName() + "! 👋");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lblGreeting.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("Explore reciprocal skill exchanges with classmates. Teach what you excel at, learn what you need.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(203, 213, 225));

        heroLeft.add(lblTag);
        heroLeft.add(Box.createRigidArea(new Dimension(0, 6)));
        heroLeft.add(lblGreeting);
        heroLeft.add(Box.createRigidArea(new Dimension(0, 6)));
        heroLeft.add(lblDesc);

        JPanel heroRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        heroRight.setOpaque(false);

        ModernButton btnHeroAdd = new ModernButton("+ Offer Skill", ModernButton.ButtonStyle.PRIMARY);
        btnHeroAdd.addActionListener(e -> showAddSkillDialog(SkillType.TEACH));

        ModernButton btnHeroDirectory = new ModernButton("Send Requests by Email ✉", ModernButton.ButtonStyle.SECONDARY);
        btnHeroDirectory.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            if (requestsSubTabs != null) {
                requestsSubTabs.setSelectedIndex(0);
            }
        });

        heroRight.add(btnHeroAdd);
        heroRight.add(btnHeroDirectory);

        hero.add(heroLeft, BorderLayout.WEST);
        hero.add(heroRight, BorderLayout.EAST);

        content.add(hero);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. 4 Modern Interactive & Clickable Stat Metric Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        lblDashTeach = new JLabel("0");
        lblDashLearn = new JLabel("0");
        lblDashMatches = new JLabel("0");
        lblDashRequests = new JLabel("0");

        statsRow.add(createInteractiveMetricCard(
                "🎓", "Skills I Teach", lblDashTeach,
                new Color(37, 99, 235), "Offering", "Click to manage offering skills →",
                () -> {
                    tabbedPane.setSelectedIndex(1);
                    if (cmbFilterType != null) {
                        cmbFilterType.setSelectedItem("TEACH ONLY");
                    }
                }
        ));

        statsRow.add(createInteractiveMetricCard(
                "📚", "Skills I Learn", lblDashLearn,
                new Color(79, 70, 229), "Seeking", "Click to manage sought skills →",
                () -> {
                    tabbedPane.setSelectedIndex(1);
                    if (cmbFilterType != null) {
                        cmbFilterType.setSelectedItem("LEARN ONLY");
                    }
                }
        ));

        statsRow.add(createInteractiveMetricCard(
                "🤝", "Reciprocal Matches", lblDashMatches,
                new Color(16, 185, 129), "Mutual", "Click to view mutual matches →",
                () -> tabbedPane.setSelectedIndex(2)
        ));

        statsRow.add(createInteractiveMetricCard(
                "📬", "Exchange Requests", lblDashRequests,
                new Color(245, 158, 11), "Pending", "Click to review proposals →",
                () -> tabbedPane.setSelectedIndex(3)
        ));

        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Active Learning Status & Partnerships Tracker Section
        JPanel activeSection = UITheme.createCardPanel();
        activeSection.setLayout(new BoxLayout(activeSection, BoxLayout.Y_AXIS));
        activeSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activeHeaderRow = new JPanel(new BorderLayout());
        activeHeaderRow.setOpaque(false);

        JLabel lblActiveTitle = new JLabel("🤝 Active Learning Partnerships & Status Tracker");
        lblActiveTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblActiveTitle.setForeground(new Color(15, 23, 42));

        JLabel lblActiveSub = new JLabel("Your ongoing verified skill exchange partnerships across campus.");
        lblActiveSub.setFont(UITheme.FONT_SMALL);
        lblActiveSub.setForeground(new Color(100, 116, 139));

        JPanel activeTitleGroup = new JPanel();
        activeTitleGroup.setLayout(new BoxLayout(activeTitleGroup, BoxLayout.Y_AXIS));
        activeTitleGroup.setOpaque(false);
        activeTitleGroup.add(lblActiveTitle);
        activeTitleGroup.add(Box.createRigidArea(new Dimension(0, 3)));
        activeTitleGroup.add(lblActiveSub);

        ModernButton btnRefreshActive = new ModernButton("Refresh Status", ModernButton.ButtonStyle.SECONDARY);
        btnRefreshActive.addActionListener(e -> loadActivePartnerships());

        activeHeaderRow.add(activeTitleGroup, BorderLayout.WEST);
        activeHeaderRow.add(btnRefreshActive, BorderLayout.EAST);

        activeSection.add(activeHeaderRow);
        activeSection.add(Box.createRigidArea(new Dimension(0, 14)));

        activePartnershipsContainer = new JPanel();
        activePartnershipsContainer.setLayout(new BoxLayout(activePartnershipsContainer, BoxLayout.Y_AXIS));
        activePartnershipsContainer.setOpaque(false);
        activeSection.add(activePartnershipsContainer);

        content.add(activeSection);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Quick Action Hub and Reciprocal Guide
        JPanel lowerGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        lowerGrid.setOpaque(false);
        lowerGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel quickActionsCard = UITheme.createCardPanel();
        quickActionsCard.setLayout(new BoxLayout(quickActionsCard, BoxLayout.Y_AXIS));

        JLabel lblQaTitle = new JLabel("⚡ Quick Operations Hub");
        lblQaTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQaTitle.setForeground(new Color(15, 23, 42));

        JLabel lblQaSub = new JLabel("Instant shortcuts to peer-to-peer exchange operations.");
        lblQaSub.setFont(UITheme.FONT_SMALL);
        lblQaSub.setForeground(new Color(100, 116, 139));

        quickActionsCard.add(lblQaTitle);
        quickActionsCard.add(Box.createRigidArea(new Dimension(0, 4)));
        quickActionsCard.add(lblQaSub);
        quickActionsCard.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel actionButtonsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        actionButtonsGrid.setOpaque(false);

        ModernButton btnActionTeach = new ModernButton("+ Teach New Skill", ModernButton.ButtonStyle.SECONDARY);
        btnActionTeach.addActionListener(e -> showAddSkillDialog(SkillType.TEACH));

        ModernButton btnActionLearn = new ModernButton("+ Learn New Skill", ModernButton.ButtonStyle.SECONDARY);
        btnActionLearn.addActionListener(e -> showAddSkillDialog(SkillType.LEARN));

        ModernButton btnActionFind = new ModernButton("Send Requests by Email ✉", ModernButton.ButtonStyle.PRIMARY);
        btnActionFind.addActionListener(e -> {
            tabbedPane.setSelectedIndex(3);
            if (requestsSubTabs != null) {
                requestsSubTabs.setSelectedIndex(0);
            }
        });

        ModernButton btnActionTree = new ModernButton("Skill Category Tree 🌳", ModernButton.ButtonStyle.SECONDARY);
        btnActionTree.addActionListener(e -> showSkillCategoryTreeDialog());

        actionButtonsGrid.add(btnActionTeach);
        actionButtonsGrid.add(btnActionLearn);
        actionButtonsGrid.add(btnActionFind);
        actionButtonsGrid.add(btnActionTree);

        quickActionsCard.add(actionButtonsGrid);

        JPanel workflowCard = UITheme.createCardPanel();
        workflowCard.setLayout(new BoxLayout(workflowCard, BoxLayout.Y_AXIS));

        JLabel lblWfTitle = new JLabel("🔄 Reciprocal Matching Flow");
        lblWfTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblWfTitle.setForeground(new Color(15, 23, 42));

        JLabel lblWfSub = new JLabel("How Skill Chain matches you with campus study partners.");
        lblWfSub.setFont(UITheme.FONT_SMALL);
        lblWfSub.setForeground(new Color(100, 116, 139));

        workflowCard.add(lblWfTitle);
        workflowCard.add(Box.createRigidArea(new Dimension(0, 4)));
        workflowCard.add(lblWfSub);
        workflowCard.add(Box.createRigidArea(new Dimension(0, 14)));

        JLabel lblSteps = new JLabel("<html>" +
                "<p style='margin-bottom:6px;'><b>1. Browse Emails:</b> View classmate emails and click 'Send Request ✉' directly next to their email.</p>" +
                "<p style='margin-bottom:6px;'><b>2. Pick Skills:</b> Choose what you offer to teach and what you want them to teach you.</p>" +
                "<p><b>3. Direct Exchange:</b> Partner approves the request and it enters your Active Partnerships!</p>" +
                "</html>");
        lblSteps.setFont(UITheme.FONT_BODY);
        lblSteps.setForeground(new Color(51, 65, 85));

        workflowCard.add(lblSteps);
        workflowCard.add(Box.createVerticalGlue());

        JPanel dbStatusPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        dbStatusPill.setOpaque(false);
        JLabel lblDot = new JLabel("●");
        lblDot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDot.setForeground(new Color(16, 185, 129));
        JLabel lblDb = new JLabel("MySQL Database Connected: skill_chain_db");
        lblDb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDb.setForeground(new Color(100, 116, 139));
        dbStatusPill.add(lblDot);
        dbStatusPill.add(lblDb);

        workflowCard.add(dbStatusPill);

        lowerGrid.add(quickActionsCard);
        lowerGrid.add(workflowCard);

        content.add(lowerGrid);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInteractiveMetricCard(String icon, String title, JLabel valueLabel,
                                                Color accentColor, String badgeText,
                                                String hintText, Runnable onClickAction) {

        class InteractiveCard extends JPanel {
            boolean isHovered = false;
            boolean isPressed = false;

            InteractiveCard() {
                setOpaque(false);
                setLayout(new BorderLayout());
                setBorder(new EmptyBorder(16, 18, 16, 18));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (isPressed) {
                            isPressed = false;
                            repaint();
                            if (isHovered && onClickAction != null) {
                                onClickAction.run();
                            }
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                int shadowOffset = isHovered ? 4 : 2;
                int shadowAlpha = isHovered ? 28 : 10;
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), shadowAlpha));
                g2.fill(new RoundRectangle2D.Float(1, 1 + shadowOffset, w - 2, h - 2, 14, 14));

                if (isPressed) {
                    g2.setColor(new Color(245, 247, 250));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12));

                int stripWidth = isHovered ? 7 : 5;
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(1, 1, stripWidth, h - 2, 6, 6));

                if (isHovered) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 140));
                    g2.setStroke(new BasicStroke(1.5f));
                } else {
                    g2.setColor(new Color(226, 232, 240));
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12));
                g2.dispose();
            }
        }

        InteractiveCard card = new InteractiveCard();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTitle = new JLabel(icon + " " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(51, 65, 85));

        JPanel badgePill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 22));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
        };
        badgePill.setOpaque(false);
        badgePill.setBorder(new EmptyBorder(2, 8, 2, 8));
        JLabel lblBadge = new JLabel(badgeText);
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(accentColor);
        badgePill.add(lblBadge);

        top.add(lblTitle, BorderLayout.WEST);
        top.add(badgePill, BorderLayout.EAST);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setBorder(new EmptyBorder(8, 0, 4, 0));

        JLabel lblHint = new JLabel(hintText);
        lblHint.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHint.setForeground(accentColor);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblHint, BorderLayout.SOUTH);

        return card;
    }

    // ========================================================================
    // SCREEN 2: MY SKILLS
    // ========================================================================
    private JPanel createMySkillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JLabel lblHeading = new JLabel("My Skill Portfolio");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Catalog the skills you can offer to teach and the skills you are seeking to learn.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(new Color(100, 116, 139));

        topContainer.add(lblHeading);
        topContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        topContainer.add(lblSub);
        topContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        summaryRow.setOpaque(false);

        lblTeachCount = new JLabel("Teaching: 0 skills");
        lblTeachCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTeachCount.setForeground(new Color(5, 150, 105));

        lblLearnCount = new JLabel("Learning: 0 skills");
        lblLearnCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLearnCount.setForeground(new Color(37, 99, 235));

        summaryRow.add(lblTeachCount);
        summaryRow.add(new JLabel("•"));
        summaryRow.add(lblLearnCount);

        topContainer.add(summaryRow);
        topContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchControls.setOpaque(false);

        searchControls.add(new JLabel("Filter:"));
        cmbFilterType = new JComboBox<>(new String[]{"ALL SKILLS", "TEACH ONLY", "LEARN ONLY"});
        cmbFilterType.setFont(UITheme.FONT_BODY);
        cmbFilterType.addActionListener(e -> filterUserSkillsTable());
        searchControls.add(cmbFilterType);

        txtSearchSkill = new ModernTextField("Search skills or category...");
        txtSearchSkill.setPreferredSize(new Dimension(200, 38));
        txtSearchSkill.addCaretListener(e -> filterUserSkillsTable());
        searchControls.add(txtSearchSkill);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonGroup.setOpaque(false);

        ModernButton btnTree = new ModernButton("View Skill Tree (Recursion)", ModernButton.ButtonStyle.SECONDARY);
        btnTree.addActionListener(e -> showSkillCategoryTreeDialog());

        ModernButton btnAddSkill = new ModernButton("+ Add Skill", ModernButton.ButtonStyle.PRIMARY);
        btnAddSkill.addActionListener(e -> showAddSkillDialog(null));

        ModernButton btnDeleteSkill = new ModernButton("Remove Selected", ModernButton.ButtonStyle.DANGER);
        btnDeleteSkill.addActionListener(e -> handleRemoveSelectedSkill());

        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> loadUserSkillsData());

        buttonGroup.add(btnTree);
        buttonGroup.add(btnRefresh);
        buttonGroup.add(btnDeleteSkill);
        buttonGroup.add(btnAddSkill);

        actionRow.add(searchControls, BorderLayout.WEST);
        actionRow.add(buttonGroup, BorderLayout.EAST);

        topContainer.add(actionRow);
        panel.add(topContainer, BorderLayout.NORTH);

        String[] columnHeaders = {"Skill Mapping ID", "Skill Name", "Category", "Exchange Type", "Date Added"};
        skillsTableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMySkills = new JTable(skillsTableModel);
        tblMySkills.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMySkills.setRowHeight(38);
        tblMySkills.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMySkills.setShowGrid(false);
        tblMySkills.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tblMySkills.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(51, 65, 85));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblMySkills.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblMySkills.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tblMySkills.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String type = value != null ? value.toString() : "";
                boolean isTeach = "TEACH".equalsIgnoreCase(type);

                JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                badgePanel.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                JPanel pill = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth();
                        int h = getHeight();

                        if (isTeach) {
                            g2.setColor(new Color(236, 253, 245));
                            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));
                            g2.setColor(new Color(167, 243, 208));
                            g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14));
                        } else {
                            g2.setColor(new Color(239, 246, 255));
                            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));
                            g2.setColor(new Color(191, 219, 254));
                            g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 14, 14));
                        }
                        g2.dispose();
                    }
                };
                pill.setOpaque(false);
                pill.setBorder(new EmptyBorder(3, 14, 3, 14));

                JLabel lbl = new JLabel(isTeach ? "TEACH" : "LEARN");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(isTeach ? new Color(5, 150, 105) : new Color(37, 99, 235));
                pill.add(lbl);

                badgePanel.add(pill);
                return badgePanel;
            }
        });

        tblMySkills.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblMySkills);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        tableWrapper.setOpaque(false);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    // ========================================================================
    // SCREEN 3: CAMPUS DIRECTORY & RECIPROCAL MATCHES
    // ========================================================================
    private JPanel createDirectoryAndMatchesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JLabel lblHeading = new JLabel("Campus Student Directory & Reciprocal Matches");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Browse student emails and click 'Send Request ✉' directly next to their email.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(new Color(100, 116, 139));

        topContainer.add(lblHeading);
        topContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        topContainer.add(lblSub);
        topContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(topContainer, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Sub Tab 1: Student Emails with Direct "Send Request" Button
        peerCardsContainer = new JPanel();
        peerCardsContainer.setLayout(new BoxLayout(peerCardsContainer, BoxLayout.Y_AXIS));
        peerCardsContainer.setBackground(new Color(248, 250, 252));
        peerCardsContainer.setBorder(new EmptyBorder(12, 10, 12, 10));

        JPanel dirPanel = createStudentEmailDirectoryPanel(peerCardsContainer, true);
        subTabs.addTab("  Student Emails & Send Request  ", dirPanel);

        // Sub Tab 2: Smart Reciprocal Matches
        subTabs.addTab("  Smart Reciprocal Matches  ", createReciprocalMatchesSubPanel());

        panel.add(subTabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds the Student Email Directory View where each student card prominently shows
     * their Name, Student Email, and a direct "Send Request" button right next to it!
     */
    private JPanel createStudentEmailDirectoryPanel(JPanel container, boolean hasSearchBar) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        if (hasSearchBar) {
            JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            searchControls.setOpaque(false);

            txtSearchPeers = new ModernTextField("Search student name, email, or skill...");
            txtSearchPeers.setPreferredSize(new Dimension(300, 38));
            txtSearchPeers.addCaretListener(e -> filterPeerDirectoryCards());
            searchControls.add(txtSearchPeers);
            toolbar.add(searchControls, BorderLayout.WEST);
        } else {
            JLabel lblInstruction = new JLabel("Click 'Send Request' next to any student's email to propose an exchange:");
            lblInstruction.setFont(UITheme.FONT_BODY_BOLD);
            lblInstruction.setForeground(new Color(30, 58, 138));
            toolbar.add(lblInstruction, BorderLayout.WEST);
        }

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        ModernButton btnRefresh = new ModernButton("Refresh Emails", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> loadPeerDirectory());
        btnBar.add(btnRefresh);

        toolbar.add(btnBar, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(new LineBorder(new Color(226, 232, 240)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReciprocalMatchesSubPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);

        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftStatus.setOpaque(false);

        lblMatchesCount = new JLabel("Mutual Matches: 0");
        lblMatchesCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMatchesCount.setForeground(new Color(5, 150, 105));

        txtSearchMatches = new ModernTextField("Search partner or skill...");
        txtSearchMatches.setPreferredSize(new Dimension(220, 38));
        txtSearchMatches.addCaretListener(e -> filterMatchesTable());

        leftStatus.add(lblMatchesCount);
        leftStatus.add(new JLabel("•"));
        leftStatus.add(txtSearchMatches);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        ModernButton btnRefreshMatches = new ModernButton("Refresh", ModernButton.ButtonStyle.SECONDARY);
        btnRefreshMatches.addActionListener(e -> loadReciprocalMatches());

        ModernButton btnSendRequest = new ModernButton("Send Swap Invitation ✉", ModernButton.ButtonStyle.PRIMARY);
        btnSendRequest.addActionListener(e -> handleSendSwapInvitation());

        rightButtons.add(btnRefreshMatches);
        rightButtons.add(btnSendRequest);

        actionRow.add(leftStatus, BorderLayout.WEST);
        actionRow.add(rightButtons, BorderLayout.EAST);
        panel.add(actionRow, BorderLayout.NORTH);

        String[] columnHeaders = {
                "Partner ID", "Offer Skill ID", "Req Skill ID",
                "Classmate", "Student Email", "You Teach (Offered)", "They Teach (Requested)"
        };

        matchesTableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMatches = new JTable(matchesTableModel);
        tblMatches.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMatches.setRowHeight(40);
        tblMatches.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMatches.setShowGrid(false);

        tblMatches.getColumnModel().getColumn(0).setMinWidth(0);
        tblMatches.getColumnModel().getColumn(0).setMaxWidth(0);
        tblMatches.getColumnModel().getColumn(0).setWidth(0);
        tblMatches.getColumnModel().getColumn(1).setMinWidth(0);
        tblMatches.getColumnModel().getColumn(1).setMaxWidth(0);
        tblMatches.getColumnModel().getColumn(1).setWidth(0);
        tblMatches.getColumnModel().getColumn(2).setMinWidth(0);
        tblMatches.getColumnModel().getColumn(2).setMaxWidth(0);
        tblMatches.getColumnModel().getColumn(2).setWidth(0);

        JTableHeader matchHeader = tblMatches.getTableHeader();
        matchHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        matchHeader.setBackground(new Color(241, 245, 249));
        matchHeader.setForeground(new Color(51, 65, 85));
        matchHeader.setPreferredSize(new Dimension(matchHeader.getWidth(), 38));

        tblMatches.getColumnModel().getColumn(5).setCellRenderer(createSkillBadgeRenderer(new Color(236, 253, 245), new Color(5, 150, 105), new Color(167, 243, 208)));
        tblMatches.getColumnModel().getColumn(6).setCellRenderer(createSkillBadgeRenderer(new Color(238, 242, 255), new Color(79, 70, 229), new Color(199, 210, 254)));

        tblMatches.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblMatches);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        tableWrapper.setOpaque(false);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableCellRenderer createSkillBadgeRenderer(Color bgColor, Color textColor, Color borderColor) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String text = value != null ? value.toString() : "";
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
                panel.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                JPanel pill = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bgColor);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.setColor(borderColor);
                        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                        g2.dispose();
                    }
                };
                pill.setOpaque(false);
                pill.setBorder(new EmptyBorder(2, 12, 2, 12));

                JLabel lbl = new JLabel(text);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(textColor);
                pill.add(lbl);

                panel.add(pill);
                return panel;
            }
        };
    }

    // ========================================================================
    // SCREEN 4: LEARNING REQUESTS (WITH DIRECT SEND REQUEST TAB)
    // ========================================================================
    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel lblHeading = new JLabel("Learning Requests & Exchange Proposals");
        lblHeading.setFont(UITheme.FONT_TITLE);
        lblHeading.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Send proposals to student emails, accept incoming offers, and monitor active requests.");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(new Color(100, 116, 139));

        titleBlock.add(lblHeading);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBlock.add(lblSub);

        JPanel topActionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActionGroup.setOpaque(false);

        ModernButton btnTopSend = new ModernButton("+ Send Request to Student", ModernButton.ButtonStyle.PRIMARY);
        btnTopSend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTopSend.setPreferredSize(new Dimension(230, 40));
        btnTopSend.setToolTipText("Choose a student email and send an exchange proposal");
        btnTopSend.addActionListener(e -> openGeneralSendRequestDialog());
        topActionGroup.add(btnTopSend);

        topContainer.add(titleBlock, BorderLayout.WEST);
        topContainer.add(topActionGroup, BorderLayout.EAST);
        topContainer.setBorder(new EmptyBorder(0, 0, 14, 0));

        panel.add(topContainer, BorderLayout.NORTH);

        requestsSubTabs = new JTabbedPane();
        requestsSubTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // TAB 1: Send Request (Displays student emails with direct "Send Request" button)
        requestsPeerCardsContainer = new JPanel();
        requestsPeerCardsContainer.setLayout(new BoxLayout(requestsPeerCardsContainer, BoxLayout.Y_AXIS));
        requestsPeerCardsContainer.setBackground(new Color(248, 250, 252));
        requestsPeerCardsContainer.setBorder(new EmptyBorder(12, 10, 12, 10));

        JPanel sendPanel = createStudentEmailDirectoryPanel(requestsPeerCardsContainer, false);
        requestsSubTabs.addTab("  Send Request (Student Emails)  ", sendPanel);

        // TAB 2: Incoming Proposals
        requestsSubTabs.addTab("  Incoming Proposals (From Classmates)  ", createIncomingRequestsSubPanel());

        // TAB 3: Outgoing Proposals
        requestsSubTabs.addTab("  Outgoing Proposals (Sent by You)  ", createOutgoingRequestsSubPanel());

        panel.add(requestsSubTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createIncomingRequestsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        lblIncomingCount = new JLabel("Pending Proposals: 0");
        lblIncomingCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblIncomingCount.setForeground(new Color(245, 158, 11));

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        ModernButton btnGoSend = new ModernButton("+ Send Request to Student", ModernButton.ButtonStyle.PRIMARY);
        btnGoSend.addActionListener(e -> {
            if (requestsSubTabs != null) {
                requestsSubTabs.setSelectedIndex(0);
            }
        });

        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> loadRequestsData());

        ModernButton btnAccept = new ModernButton("Accept Exchange ✓", ModernButton.ButtonStyle.SUCCESS);
        btnAccept.addActionListener(e -> handleAcceptRequest());

        ModernButton btnReject = new ModernButton("Decline ✗", ModernButton.ButtonStyle.DANGER);
        btnReject.addActionListener(e -> handleRejectRequest());

        btnBar.add(btnGoSend);
        btnBar.add(btnRefresh);
        btnBar.add(btnReject);
        btnBar.add(btnAccept);

        toolbar.add(lblIncomingCount, BorderLayout.WEST);
        toolbar.add(btnBar, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] headers = {"Request ID", "From Student", "They Offer to Teach", "They Want From You", "Status", "Date Received"};
        incomingTableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblIncomingRequests = new JTable(incomingTableModel);
        tblIncomingRequests.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblIncomingRequests.setRowHeight(38);
        tblIncomingRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblIncomingRequests.setShowGrid(false);

        JTableHeader header = tblIncomingRequests.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblIncomingRequests.getColumnModel().getColumn(0).setCellRenderer(center);
        tblIncomingRequests.getColumnModel().getColumn(5).setCellRenderer(center);
        tblIncomingRequests.getColumnModel().getColumn(4).setCellRenderer(createStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(tblIncomingRequests);
        scroll.setBorder(new LineBorder(new Color(226, 232, 240)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOutgoingRequestsSubPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        lblOutgoingCount = new JLabel("Total Sent: 0");
        lblOutgoingCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOutgoingCount.setForeground(new Color(37, 99, 235));

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        ModernButton btnGoSend = new ModernButton("+ Send Another Request", ModernButton.ButtonStyle.PRIMARY);
        btnGoSend.addActionListener(e -> {
            if (requestsSubTabs != null) {
                requestsSubTabs.setSelectedIndex(0);
            }
        });

        ModernButton btnRefresh = new ModernButton("Refresh", ModernButton.ButtonStyle.SECONDARY);
        btnRefresh.addActionListener(e -> loadRequestsData());
        btnBar.add(btnGoSend);
        btnBar.add(btnRefresh);

        toolbar.add(lblOutgoingCount, BorderLayout.WEST);
        toolbar.add(btnBar, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] headers = {"Request ID", "Sent To Classmate", "You Offered to Teach", "You Requested to Learn", "Status", "Date Sent"};
        outgoingTableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblOutgoingRequests = new JTable(outgoingTableModel);
        tblOutgoingRequests.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblOutgoingRequests.setRowHeight(38);
        tblOutgoingRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOutgoingRequests.setShowGrid(false);

        JTableHeader header = tblOutgoingRequests.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblOutgoingRequests.getColumnModel().getColumn(0).setCellRenderer(center);
        tblOutgoingRequests.getColumnModel().getColumn(5).setCellRenderer(center);
        tblOutgoingRequests.getColumnModel().getColumn(4).setCellRenderer(createStatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(tblOutgoingRequests);
        scroll.setBorder(new LineBorder(new Color(226, 232, 240)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(scroll, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableCellRenderer createStatusBadgeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = value != null ? value.toString() : "PENDING";

                Color bg;
                Color text;
                Color border;

                if ("ACCEPTED".equalsIgnoreCase(status)) {
                    bg = new Color(236, 253, 245);
                    text = new Color(5, 150, 105);
                    border = new Color(167, 243, 208);
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    bg = new Color(254, 242, 242);
                    text = new Color(220, 38, 38);
                    border = new Color(254, 202, 202);
                } else {
                    bg = new Color(254, 243, 199);
                    text = new Color(180, 83, 9);
                    border = new Color(253, 230, 138);
                }

                JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
                badgePanel.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                JPanel pill = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bg);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                        g2.setColor(border);
                        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                        g2.dispose();
                    }
                };
                pill.setOpaque(false);
                pill.setBorder(new EmptyBorder(2, 12, 2, 12));

                JLabel lbl = new JLabel(status.toUpperCase());
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(text);
                pill.add(lbl);

                badgePanel.add(pill);
                return badgePanel;
            }
        };
    }

    // ========================================================================
    // NOTIFICATION CENTER MODAL DIALOG
    // ========================================================================
    private void showNotificationsDialog() {
        JDialog dialog = new JDialog(this, "Notifications & Exchange Activity", true);
        dialog.setSize(620, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("🔔 Notifications Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Live updates on accepted exchange proposals and incoming requests.");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(lblTitle);
        headerText.add(Box.createRigidArea(new Dimension(0, 3)));
        headerText.add(lblSub);

        header.add(headerText, BorderLayout.WEST);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(248, 250, 252));
        listContainer.setBorder(new EmptyBorder(14, 16, 14, 16));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        if (cachedNotifications.isEmpty()) {
            JPanel emptyCard = UITheme.createCardPanel();
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            JLabel lblEmpty = new JLabel("No new notifications right now.");
            lblEmpty.setFont(UITheme.FONT_BODY);
            lblEmpty.setForeground(new Color(100, 116, 139));
            emptyCard.add(lblEmpty);
            listContainer.add(emptyCard);
        } else {
            for (AppNotification n : cachedNotifications) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(Color.WHITE);
                card.setBorder(new CompoundBorder(
                        new LineBorder(n.getType() == AppNotification.NotificationType.ACCEPTED ? new Color(167, 243, 208) : new Color(253, 230, 138), 1),
                        new EmptyBorder(12, 14, 12, 14)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

                JPanel textCol = new JPanel();
                textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
                textCol.setOpaque(false);

                JLabel lblItemTitle = new JLabel(n.getTitle());
                lblItemTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblItemTitle.setForeground(n.getType() == AppNotification.NotificationType.ACCEPTED ? new Color(5, 150, 105) : new Color(180, 83, 9));

                JLabel lblMsg = new JLabel("<html>" + n.getMessage() + "</html>");
                lblMsg.setFont(UITheme.FONT_BODY);
                lblMsg.setForeground(new Color(51, 65, 85));

                JLabel lblTime = new JLabel(n.getTimestamp().format(dtf));
                lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lblTime.setForeground(new Color(148, 163, 184));

                textCol.add(lblItemTitle);
                textCol.add(Box.createRigidArea(new Dimension(0, 3)));
                textCol.add(lblMsg);
                textCol.add(Box.createRigidArea(new Dimension(0, 4)));
                textCol.add(lblTime);

                card.add(textCol, BorderLayout.CENTER);
                listContainer.add(card);
                listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(Color.WHITE);
        ModernButton btnClose = new ModernButton("Close", ModernButton.ButtonStyle.PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());
        footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ========================================================================
    // DIRECT PROPOSAL DIALOG (CALLED FROM "SEND REQUEST ✉" BUTTON NEXT TO EMAIL)
    // ========================================================================
    private void openDirectProposalDialog(int targetId, String targetName, String targetEmail) {
        JDialog dialog = new JDialog(this, "Send Skill Exchange Request to " + targetName, true);
        dialog.setSize(520, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 22, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel lblRecipient = new JLabel("Target Classmate: " + targetName + " (" + targetEmail + ")");
        lblRecipient.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRecipient.setForeground(new Color(37, 99, 235));
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lblRecipient, gbc);

        JLabel lblOffer = new JLabel("1. What skill do you offer to teach " + targetName + "?");
        lblOffer.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblOffer, gbc);

        JComboBox<String> cmbOffer = new JComboBox<>();
        cmbOffer.setFont(UITheme.FONT_BODY);
        List<Skill> allSkills = new ArrayList<>();
        try {
            allSkills = skillService.getAllSkills();
            for (Skill s : allSkills) {
                cmbOffer.addItem(s.getSkillName() + " (" + s.getCategory() + ")");
            }
        } catch (SQLException ignored) {
        }
        gbc.gridy = row++;
        panel.add(cmbOffer, gbc);

        JLabel lblReq = new JLabel("2. What skill do you want to learn from " + targetName + "?");
        lblReq.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblReq, gbc);

        JComboBox<String> cmbReq = new JComboBox<>();
        cmbReq.setFont(UITheme.FONT_BODY);
        for (Skill s : allSkills) {
            cmbReq.addItem(s.getSkillName() + " (" + s.getCategory() + ")");
        }
        gbc.gridy = row++;
        panel.add(cmbReq, gbc);

        final List<Skill> finalSkills = allSkills;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(new Color(248, 250, 252));

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> dialog.dispose());

        ModernButton btnSend = new ModernButton("Confirm & Send Request", ModernButton.ButtonStyle.PRIMARY);
        btnSend.addActionListener(e -> {
            int offIdx = cmbOffer.getSelectedIndex();
            int reqIdx = cmbReq.getSelectedIndex();

            if (offIdx < 0 || reqIdx < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select both skills.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Skill offSkill = finalSkills.get(offIdx);
            Skill reqSkill = finalSkills.get(reqIdx);

            if (offSkill.getSkillId() == reqSkill.getSkillId()) {
                JOptionPane.showMessageDialog(dialog, "Offered and requested skills cannot be identical.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                requestService.sendRequest(currentUser.getId(), targetId, offSkill.getSkillId(), reqSkill.getSkillId());
                JOptionPane.showMessageDialog(dialog,
                        "Exchange proposal successfully sent to " + targetName + " (" + targetEmail + ")!\n" +
                        "They can now approve it under their Incoming Proposals.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllData();

                // Automatically switch to Outgoing Proposals tab
                tabbedPane.setSelectedIndex(3);
                if (requestsSubTabs != null) {
                    requestsSubTabs.setSelectedIndex(2);
                }

            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Proposal", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(btnCancel);
        btnRow.add(btnSend);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * General Send Request Dialog: lets the student select any classmate email from a dropdown
     * and select offered & requested skills to send proposals directly.
     */
    private void openGeneralSendRequestDialog() {
        if (cachedPeerStudents.isEmpty()) {
            try {
                cachedPeerStudents = requestService.getPeerDirectory(currentUser.getId());
            } catch (SQLException ignored) {}
        }
        if (cachedPeerStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classmate accounts found to send requests to.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Send Skill Exchange Request to Classmate", true);
        dialog.setSize(560, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 22, 18, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel lblSelectPeer = new JLabel("1. Select Student Email to send request to:");
        lblSelectPeer.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lblSelectPeer, gbc);

        JComboBox<String> cmbPeers = new JComboBox<>();
        cmbPeers.setFont(UITheme.FONT_BODY);
        for (PeerStudent p : cachedPeerStudents) {
            cmbPeers.addItem(p.getName() + " — (" + p.getEmail() + ")");
        }
        gbc.gridy = row++;
        panel.add(cmbPeers, gbc);

        JLabel lblOffer = new JLabel("2. What skill do you offer to teach them?");
        lblOffer.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblOffer, gbc);

        JComboBox<String> cmbOffer = new JComboBox<>();
        cmbOffer.setFont(UITheme.FONT_BODY);
        List<Skill> allSkills = new ArrayList<>();
        try {
            allSkills = skillService.getAllSkills();
            for (Skill s : allSkills) {
                cmbOffer.addItem(s.getSkillName() + " (" + s.getCategory() + ")");
            }
        } catch (SQLException ignored) {
        }
        gbc.gridy = row++;
        panel.add(cmbOffer, gbc);

        JLabel lblReq = new JLabel("3. What skill do you want to learn from them?");
        lblReq.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblReq, gbc);

        JComboBox<String> cmbReq = new JComboBox<>();
        cmbReq.setFont(UITheme.FONT_BODY);
        for (Skill s : allSkills) {
            cmbReq.addItem(s.getSkillName() + " (" + s.getCategory() + ")");
        }
        gbc.gridy = row++;
        panel.add(cmbReq, gbc);

        final List<Skill> finalSkills = allSkills;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(new Color(248, 250, 252));

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> dialog.dispose());

        ModernButton btnSend = new ModernButton("Confirm & Send Request", ModernButton.ButtonStyle.PRIMARY);
        btnSend.addActionListener(e -> {
            int peerIdx = cmbPeers.getSelectedIndex();
            int offIdx = cmbOffer.getSelectedIndex();
            int reqIdx = cmbReq.getSelectedIndex();

            if (peerIdx < 0 || offIdx < 0 || reqIdx < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select classmate and both skills.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PeerStudent targetPeer = cachedPeerStudents.get(peerIdx);
            Skill offSkill = finalSkills.get(offIdx);
            Skill reqSkill = finalSkills.get(reqIdx);

            if (offSkill.getSkillId() == reqSkill.getSkillId()) {
                JOptionPane.showMessageDialog(dialog, "Offered and requested skills cannot be identical.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                requestService.sendRequest(currentUser.getId(), targetPeer.getUserId(), offSkill.getSkillId(), reqSkill.getSkillId());
                JOptionPane.showMessageDialog(dialog,
                        "Exchange proposal successfully sent to " + targetPeer.getName() + " (" + targetPeer.getEmail() + ")!\n" +
                        "They can now approve it under their Incoming Proposals.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllData();

                // Automatically switch to Outgoing Proposals tab
                tabbedPane.setSelectedIndex(3);
                if (requestsSubTabs != null) {
                    requestsSubTabs.setSelectedIndex(2);
                }

            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Proposal", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(btnCancel);
        btnRow.add(btnSend);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ========================================================================
    // DATA SYNCHRONIZATION & RENDERING
    // ========================================================================

    private void refreshAllData() {
        loadUserSkillsData();
        loadPeerDirectory();
        loadReciprocalMatches();
        loadRequestsData();
        loadActivePartnerships();
        refreshDashboardStats();
        refreshNotifications();
    }

    private void refreshDashboardStats() {
        try {
            int teachCount = 0;
            int learnCount = 0;
            List<UserSkill> skills = skillService.getUserSkills(currentUser.getId());
            for (UserSkill s : skills) {
                if (s.getSkillType() == SkillType.TEACH) teachCount++;
                else learnCount++;
            }
            lblDashTeach.setText(String.valueOf(teachCount));
            lblDashLearn.setText(String.valueOf(learnCount));

            int matchCount = matchingService.getReciprocalMatchCount(currentUser.getId());
            lblDashMatches.setText(String.valueOf(matchCount));

            int pendingReqCount = requestService.getPendingIncomingCount(currentUser.getId());
            lblDashRequests.setText(String.valueOf(pendingReqCount));

        } catch (SQLException ignored) {
        }
    }

    private void refreshNotifications() {
        try {
            cachedNotifications = requestService.getNotifications(currentUser.getId());
            notificationCount = cachedNotifications.size();
            if (btnNotifications != null) {
                btnNotifications.setToolTipText("Notifications (" + notificationCount + ")");
                btnNotifications.repaint();
            }
        } catch (SQLException ignored) {
        }
    }

    private void loadActivePartnerships() {
        if (activePartnershipsContainer == null) return;
        activePartnershipsContainer.removeAll();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        try {
            List<ActivePartnership> partnerships = requestService.getActivePartnerships(currentUser.getId());
            if (partnerships.isEmpty()) {
                JPanel emptyCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
                emptyCard.setOpaque(false);
                JLabel lblEmpty = new JLabel("<html><i>No active partnerships yet. Use the <b>Send Request (Student Emails)</b> tab to connect with classmates!</i></html>");
                lblEmpty.setFont(UITheme.FONT_BODY);
                lblEmpty.setForeground(new Color(100, 116, 139));
                emptyCard.add(lblEmpty);
                activePartnershipsContainer.add(emptyCard);
            } else {
                for (ActivePartnership ap : partnerships) {
                    JPanel pCard = new JPanel(new BorderLayout());
                    pCard.setBackground(new Color(248, 250, 252));
                    pCard.setBorder(new CompoundBorder(
                            new LineBorder(new Color(209, 250, 229), 1),
                            new EmptyBorder(10, 14, 10, 14)
                    ));
                    pCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

                    JPanel left = new JPanel();
                    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                    left.setOpaque(false);

                    JLabel lblPName = new JLabel("Partner: " + ap.getPartnerName() + " (" + ap.getPartnerEmail() + ")");
                    lblPName.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    lblPName.setForeground(new Color(15, 23, 42));

                    JLabel lblExchange = new JLabel("You Teach: " + ap.getYouTeachSkill() + "  ⇄  They Teach: " + ap.getPartnerTeachesSkill());
                    lblExchange.setFont(UITheme.FONT_BODY);
                    lblExchange.setForeground(new Color(37, 99, 235));

                    left.add(lblPName);
                    left.add(Box.createRigidArea(new Dimension(0, 3)));
                    left.add(lblExchange);

                    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
                    right.setOpaque(false);

                    JPanel statusBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 3));
                    statusBadge.setBackground(new Color(236, 253, 245));
                    statusBadge.setBorder(new LineBorder(new Color(167, 243, 208)));

                    JLabel lblStatus = new JLabel("● ACTIVE PARTNERSHIP");
                    lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lblStatus.setForeground(new Color(5, 150, 105));
                    statusBadge.add(lblStatus);

                    JLabel lblDate = new JLabel("Since: " + ap.getEstablishedDate().format(dtf));
                    lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    lblDate.setForeground(new Color(148, 163, 184));

                    right.add(statusBadge);
                    right.add(lblDate);

                    pCard.add(left, BorderLayout.WEST);
                    pCard.add(right, BorderLayout.EAST);

                    activePartnershipsContainer.add(pCard);
                    activePartnershipsContainer.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
        } catch (SQLException e) {
            JLabel lblErr = new JLabel("Failed to load active status: " + e.getMessage());
            lblErr.setForeground(Color.RED);
            activePartnershipsContainer.add(lblErr);
        }

        activePartnershipsContainer.revalidate();
        activePartnershipsContainer.repaint();
    }

    private void loadPeerDirectory() {
        try {
            cachedPeerStudents = requestService.getPeerDirectory(currentUser.getId());
            if (peerCardsContainer != null) {
                renderStudentEmailCards(peerCardsContainer, txtSearchPeers != null ? txtSearchPeers.getText() : "");
            }
            if (requestsPeerCardsContainer != null) {
                renderStudentEmailCards(requestsPeerCardsContainer, "");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load student emails: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterPeerDirectoryCards() {
        String query = txtSearchPeers != null ? txtSearchPeers.getText().trim() : "";
        if (peerCardsContainer != null) {
            renderStudentEmailCards(peerCardsContainer, query);
        }
    }

    /**
     * Renders each student in a card showing their Name, Email in bold blue,
     * skills, and a prominent "Send Request ✉" button right next to the email!
     */
    private void renderStudentEmailCards(JPanel container, String query) {
        container.removeAll();
        String q = query != null ? query.trim().toLowerCase() : "";

        List<PeerStudent> filtered = new ArrayList<>();
        for (PeerStudent p : cachedPeerStudents) {
            if (q.isEmpty() ||
                    p.getName().toLowerCase().contains(q) ||
                    p.getEmail().toLowerCase().contains(q) ||
                    p.getTeachingSkills().toLowerCase().contains(q) ||
                    p.getLearningSkills().toLowerCase().contains(q)) {
                filtered.add(p);
            }
        }

        if (filtered.isEmpty()) {
            JPanel emptyCard = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyCard.setOpaque(false);
            JLabel lblEmpty = new JLabel("No student accounts found matching '" + q + "'.");
            lblEmpty.setFont(UITheme.FONT_BODY);
            lblEmpty.setForeground(new Color(100, 116, 139));
            emptyCard.add(lblEmpty);
            container.add(emptyCard);
        } else {
            for (PeerStudent p : filtered) {
                container.add(createStudentEmailCard(p));
                container.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel createStudentEmailCard(PeerStudent p) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 142));
        card.setPreferredSize(new Dimension(850, 130));
        card.setMinimumSize(new Dimension(450, 120));

        // 1. Left Avatar circle with initials
        String initials = getInitials(p.getName());
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239, 246, 255));
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(new Color(37, 99, 235));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, 41, 41);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (42 - fm.stringWidth(initials)) / 2;
                int y = (42 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(42, 42));
        avatarCircle.setOpaque(false);

        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        avatarWrapper.setOpaque(false);
        avatarWrapper.add(avatarCircle);

        // 2. Center info block
        JPanel centerInfo = new JPanel();
        centerInfo.setLayout(new BoxLayout(centerInfo, BoxLayout.Y_AXIS));
        centerInfo.setOpaque(false);

        // Line 1: Student Name & Peer Badge
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameRow.setOpaque(false);

        JLabel lblName = new JLabel(p.getName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setForeground(new Color(15, 23, 42));

        JPanel peerBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        peerBadge.setBackground(new Color(241, 245, 249));
        peerBadge.setBorder(new LineBorder(new Color(203, 213, 225)));
        JLabel lblPeerTag = new JLabel("Classmate");
        lblPeerTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPeerTag.setForeground(new Color(71, 85, 105));
        peerBadge.add(lblPeerTag);

        nameRow.add(lblName);
        nameRow.add(peerBadge);

        // Line 2: Prominent Email Pill WITH Direct "Send Request" Button Right Beside It!
        JPanel emailActionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        emailActionRow.setOpaque(false);

        JPanel emailPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        emailPill.setBackground(new Color(239, 246, 255));
        emailPill.setBorder(new CompoundBorder(
                new LineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(1, 8, 1, 8)
        ));
        JLabel lblEmail = new JLabel("Email: " + p.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setForeground(new Color(29, 78, 216));
        emailPill.add(lblEmail);

        ModernButton btnSendDirect = new ModernButton("Send Request", ModernButton.ButtonStyle.PRIMARY);
        btnSendDirect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSendDirect.setPreferredSize(new Dimension(140, 32));
        btnSendDirect.setToolTipText("Send peer skill exchange request directly to " + p.getEmail());
        btnSendDirect.addActionListener(e -> openDirectProposalDialog(p.getUserId(), p.getName(), p.getEmail()));

        emailActionRow.add(emailPill);
        emailActionRow.add(btnSendDirect);

        // Line 3: Skills offered and sought (Clearly visible badges with full height)
        JPanel skillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        skillsRow.setOpaque(false);

        JPanel teachBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        teachBadge.setBackground(new Color(236, 253, 245));
        teachBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(167, 243, 208), 1, true),
                new EmptyBorder(1, 6, 1, 6)
        ));
        JLabel lblTeach = new JLabel("Teaches: " + p.getTeachingSkills());
        lblTeach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTeach.setForeground(new Color(5, 150, 105)); // Emerald
        teachBadge.add(lblTeach);

        JPanel learnBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        learnBadge.setBackground(new Color(241, 245, 249));
        learnBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(1, 6, 1, 6)
        ));
        JLabel lblLearn = new JLabel("Wants to Learn: " + p.getLearningSkills());
        lblLearn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLearn.setForeground(new Color(51, 65, 85));
        learnBadge.add(lblLearn);

        skillsRow.add(teachBadge);
        skillsRow.add(learnBadge);

        centerInfo.add(nameRow);
        centerInfo.add(Box.createRigidArea(new Dimension(0, 5)));
        centerInfo.add(emailActionRow);
        centerInfo.add(Box.createRigidArea(new Dimension(0, 6)));
        centerInfo.add(skillsRow);

        card.add(avatarWrapper, BorderLayout.WEST);
        card.add(centerInfo, BorderLayout.CENTER);

        return card;
    }

    private void loadUserSkillsData() {
        try {
            List<UserSkill> skills = skillService.getUserSkills(currentUser.getId());
            displaySkillsInTable(skills);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load skills from MySQL: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUserSkillsTable() {
        String filterType = (String) cmbFilterType.getSelectedItem();
        String keyword = txtSearchSkill.getText().trim().toLowerCase();

        try {
            List<UserSkill> skills;
            if ("TEACH ONLY".equals(filterType)) {
                skills = skillService.getUserSkills(currentUser.getId(), SkillType.TEACH);
            } else if ("LEARN ONLY".equals(filterType)) {
                skills = skillService.getUserSkills(currentUser.getId(), SkillType.LEARN);
            } else {
                skills = skillService.getUserSkills(currentUser.getId());
            }

            List<UserSkill> filtered = new ArrayList<>();
            for (UserSkill us : skills) {
                if (keyword.isEmpty() ||
                        us.getSkillName().toLowerCase().contains(keyword) ||
                        us.getCategory().toLowerCase().contains(keyword)) {
                    filtered.add(us);
                }
            }

            displaySkillsInTable(filtered);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering skills: " + e.getMessage(),
                    "Query Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySkillsInTable(List<UserSkill> skills) {
        skillsTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int teachCount = 0;
        int learnCount = 0;

        for (UserSkill s : skills) {
            if (s.getSkillType() == SkillType.TEACH) {
                teachCount++;
            } else {
                learnCount++;
            }

            String formattedDate = s.getCreatedAt() != null ? s.getCreatedAt().format(formatter) : "N/A";
            skillsTableModel.addRow(new Object[]{
                    s.getUserSkillId(),
                    s.getSkillName(),
                    s.getCategory(),
                    s.getSkillType().name(),
                    formattedDate
            });
        }

        lblTeachCount.setText("Teaching: " + teachCount + " skills");
        lblLearnCount.setText("Learning: " + learnCount + " skills");
        lblDashTeach.setText(String.valueOf(teachCount));
        lblDashLearn.setText(String.valueOf(learnCount));
    }

    private void loadReciprocalMatches() {
        try {
            cachedMatches = matchingService.findReciprocalMatches(currentUser.getId());
            displayMatchesInTable(cachedMatches);
            lblDashMatches.setText(String.valueOf(cachedMatches.size()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to calculate reciprocal matches: " + e.getMessage(),
                    "Matching Engine Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterMatchesTable() {
        String query = txtSearchMatches.getText().trim().toLowerCase();
        List<ReciprocalMatch> filtered = new ArrayList<>();
        for (ReciprocalMatch m : cachedMatches) {
            if (query.isEmpty() ||
                    m.getMatchedUserName().toLowerCase().contains(query) ||
                    m.getUserOfferedSkillName().toLowerCase().contains(query) ||
                    m.getPartnerOfferedSkillName().toLowerCase().contains(query)) {
                filtered.add(m);
            }
        }
        displayMatchesInTable(filtered);
    }

    private void displayMatchesInTable(List<ReciprocalMatch> matches) {
        matchesTableModel.setRowCount(0);
        for (ReciprocalMatch m : matches) {
            matchesTableModel.addRow(new Object[]{
                    m.getMatchedUserId(),
                    m.getUserOfferedSkillId(),
                    m.getPartnerOfferedSkillId(),
                    m.getMatchedUserName(),
                    m.getMatchedUserEmail(),
                    m.getUserOfferedSkillName() + " (" + m.getUserOfferedCategory() + ")",
                    m.getPartnerOfferedSkillName() + " (" + m.getPartnerOfferedCategory() + ")"
            });
        }
        lblMatchesCount.setText("Mutual Matches: " + matches.size());
    }

    private void handleSendSwapInvitation() {
        int selectedRow = tblMatches.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a mutual match from the table to send an exchange invitation.",
                    "No Match Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partnerId = (int) matchesTableModel.getValueAt(selectedRow, 0);
        int offerSkillId = (int) matchesTableModel.getValueAt(selectedRow, 1);
        int reqSkillId = (int) matchesTableModel.getValueAt(selectedRow, 2);
        String partnerName = (String) matchesTableModel.getValueAt(selectedRow, 3);
        String youTeach = (String) matchesTableModel.getValueAt(selectedRow, 5);
        String theyTeach = (String) matchesTableModel.getValueAt(selectedRow, 6);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Send a Reciprocal Skill Exchange invitation to " + partnerName + "?\n\n" +
                "• You offer to teach: " + youTeach + "\n" +
                "• You request to learn: " + theyTeach + "\n\n" +
                "This will appear in " + partnerName + "'s Learning Requests tab for approval.",
                "Confirm Exchange Proposal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestService.sendRequest(currentUser.getId(), partnerId, offerSkillId, reqSkillId);
                JOptionPane.showMessageDialog(this,
                        "Exchange proposal sent successfully to " + partnerName + "!\n" +
                        "You can track its status under the 'Learning Requests' tab.",
                        "Proposal Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Proposal", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadRequestsData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 1. Incoming Requests
        try {
            List<LearningRequest> incoming = requestService.getIncomingRequests(currentUser.getId());
            incomingTableModel.setRowCount(0);
            int pendingCount = 0;

            for (LearningRequest r : incoming) {
                if ("PENDING".equalsIgnoreCase(r.getStatus())) {
                    pendingCount++;
                }
                String dateStr = r.getCreatedAt() != null ? r.getCreatedAt().format(formatter) : "N/A";
                incomingTableModel.addRow(new Object[]{
                        r.getRequestId(),
                        r.getSenderName(),
                        r.getOfferedSkillName(),
                        r.getRequestedSkillName(),
                        r.getStatus(),
                        dateStr
                });
            }

            lblIncomingCount.setText("Pending Proposals: " + pendingCount + " (Total: " + incoming.size() + ")");
            lblDashRequests.setText(String.valueOf(pendingCount));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load incoming requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // 2. Outgoing Requests
        try {
            List<LearningRequest> outgoing = requestService.getOutgoingRequests(currentUser.getId());
            outgoingTableModel.setRowCount(0);

            for (LearningRequest r : outgoing) {
                String dateStr = r.getCreatedAt() != null ? r.getCreatedAt().format(formatter) : "N/A";
                outgoingTableModel.addRow(new Object[]{
                        r.getRequestId(),
                        r.getReceiverName(),
                        r.getOfferedSkillName(),
                        r.getRequestedSkillName(),
                        r.getStatus(),
                        dateStr
                });
            }

            lblOutgoingCount.setText("Total Sent: " + outgoing.size());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load outgoing requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAcceptRequest() {
        int selectedRow = tblIncomingRequests.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an incoming request to accept.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) incomingTableModel.getValueAt(selectedRow, 0);
        String senderName = (String) incomingTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) incomingTableModel.getValueAt(selectedRow, 4);

        if (!"PENDING".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This request is already " + currentStatus + ".", "Already Processed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Accept skill exchange with " + senderName + "?\n" +
                "You both agree to swap your selected skills.",
                "Accept Proposal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestService.acceptRequest(requestId);
                JOptionPane.showMessageDialog(this,
                        "Exchange accepted! You and " + senderName + " are now active learning partners.",
                        "Exchange Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRejectRequest() {
        int selectedRow = tblIncomingRequests.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an incoming request to decline.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) incomingTableModel.getValueAt(selectedRow, 0);
        String senderName = (String) incomingTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) incomingTableModel.getValueAt(selectedRow, 4);

        if (!"PENDING".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This request is already " + currentStatus + ".", "Already Processed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to decline this exchange request from " + senderName + "?",
                "Decline Proposal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestService.rejectRequest(requestId);
                JOptionPane.showMessageDialog(this, "Exchange request declined.", "Declined", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddSkillDialog(SkillType preselectedType) {
        JDialog dialog = new JDialog(this, "Add Skill to My Profile", true);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        int row = 0;

        JLabel lblType = new JLabel("Exchange Intent");
        lblType.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(lblType, gbc);

        JComboBox<String> cmbType = new JComboBox<>(SkillType.VALUES_ARRAY);
        cmbType.setFont(UITheme.FONT_BODY);
        if (preselectedType != null) {
            cmbType.setSelectedItem(preselectedType.name());
        }
        gbc.gridy = row++;
        panel.add(cmbType, gbc);

        JLabel lblCatalog = new JLabel("Select from Catalog (or type below)");
        lblCatalog.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblCatalog, gbc);

        JComboBox<String> cmbCatalog = new JComboBox<>();
        cmbCatalog.setFont(UITheme.FONT_BODY);
        cmbCatalog.addItem("-- Choose from Catalog (Optional) --");

        try {
            List<Skill> catalogSkills = skillService.getAllSkills();
            for (Skill cs : catalogSkills) {
                cmbCatalog.addItem(cs.getSkillName() + " (" + cs.getCategory() + ")");
            }
        } catch (SQLException ignored) {
        }
        gbc.gridy = row++;
        panel.add(cmbCatalog, gbc);

        JLabel lblCustom = new JLabel("Skill Name");
        lblCustom.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblCustom, gbc);

        ModernTextField txtCustomName = new ModernTextField("e.g. Flutter, Kotlin, Spring Boot");
        gbc.gridy = row++;
        panel.add(txtCustomName, gbc);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setFont(UITheme.FONT_BODY_BOLD);
        gbc.gridy = row++;
        panel.add(lblCategory, gbc);

        JComboBox<String> cmbCategory = new JComboBox<>(Skill.CATEGORIES);
        cmbCategory.setFont(UITheme.FONT_BODY);
        gbc.gridy = row++;
        panel.add(cmbCategory, gbc);

        cmbCatalog.addActionListener(e -> {
            int idx = cmbCatalog.getSelectedIndex();
            if (idx > 0) {
                String selected = (String) cmbCatalog.getSelectedItem();
                int paren = selected.indexOf(" (");
                if (paren > 0) {
                    txtCustomName.setText(selected.substring(0, paren));
                }
            }
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonRow.setBackground(new Color(248, 250, 252));

        ModernButton btnCancel = new ModernButton("Cancel", ModernButton.ButtonStyle.SECONDARY);
        btnCancel.addActionListener(e -> dialog.dispose());

        ModernButton btnSave = new ModernButton("Save Skill", ModernButton.ButtonStyle.PRIMARY);
        btnSave.addActionListener(e -> {
            String skillName = txtCustomName.getText().trim();
            if (skillName.isEmpty() && cmbCatalog.getSelectedIndex() > 0) {
                String selected = (String) cmbCatalog.getSelectedItem();
                int paren = selected.indexOf(" (");
                skillName = paren > 0 ? selected.substring(0, paren) : selected;
            }

            if (skillName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter or select a skill name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedCat = (String) cmbCategory.getSelectedItem();
            SkillType selectedType = SkillType.fromString((String) cmbType.getSelectedItem());

            try {
                skillService.addCustomUserSkill(currentUser.getId(), skillName, selectedCat, selectedType);
                JOptionPane.showMessageDialog(dialog, "Skill added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAllData();
            } catch (DuplicateSkillException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Skill", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleRemoveSelectedSkill() {
        int selectedRow = tblMySkills.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a skill from the table to remove.",
                    "No Skill Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userSkillId = (int) skillsTableModel.getValueAt(selectedRow, 0);
        String skillName = (String) skillsTableModel.getValueAt(selectedRow, 1);
        String type = (String) skillsTableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove '" + skillName + "' (" + type + ") from your profile?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                skillService.removeUserSkill(userSkillId, currentUser.getId());
                JOptionPane.showMessageDialog(this, "Skill removed successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to remove skill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSkillCategoryTreeDialog() {
        JDialog dialog = new JDialog(this, "Academic Skill Taxonomy (Recursive Tree Traversal)", true);
        dialog.setSize(560, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea txtTree = new JTextArea();
        txtTree.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtTree.setEditable(false);
        txtTree.setBackground(new Color(248, 250, 252));
        txtTree.setBorder(new EmptyBorder(14, 16, 14, 16));

        String treeText = skillService.getFormattedSkillTaxonomyTree();
        txtTree.setText(treeText);
        txtTree.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(txtTree);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        closePanel.setBackground(Color.WHITE);
        ModernButton btnClose = new ModernButton("Close", ModernButton.ButtonStyle.PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());
        closePanel.add(btnClose);
        dialog.add(closePanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of Skill Chain?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            loginFrame.setVisible(true);
        }
    }
}
