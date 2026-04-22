package ui;

import service.PerformanceService;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PerformanceManagementUI extends JFrame {
    private JPanel sidebar;
    private JPanel content;
    private CardLayout cardLayout;
    private Map<String, JPanel> navItems = new HashMap<>();
    private String currentPage = "dashboard";

    public PerformanceManagementUI() {
        setTitle("Performance Pulse — HRMS Integrated Subsystem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setBackground(Theme.BG);
        getContentPane().setBackground(Theme.BG);

        // Pre-load service to ensure DB is initialized and data is seeded
        System.out.println("Initializing Performance Service...");
        PerformanceService service = PerformanceService.getInstance();
        System.out.println("Loaded " + service.getAllEmployees().size() + " employees from HRMS database.");

        setLayout(new BorderLayout());

        initSidebar();
        initContent();

        showPage("dashboard");
        setVisible(true);
    }

    private void initSidebar() {
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(Theme.SUR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BOR));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(220, 120));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));

        JLabel logoT = new JLabel("<html>Perform<br/>Pulse</html>");
        logoT.setFont(Theme.SERIF_MD);
        logoT.setForeground(Theme.ACC);
        logoPanel.add(logoT);

        JLabel logoS = new JLabel("HRMS · LIVE");
        logoS.setFont(Theme.SANS_XS);
        logoS.setForeground(Theme.MU);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        logoPanel.add(logoS);

        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // Nav
        String[][] navConfigs = {
            {"📊", "Dashboard", "dashboard"},
            {"👥", "Employees", "employees"},
            {"🎯", "Goals", "goals"},
            {"📈", "KPIs", "kpis"},
            {"💬", "Feedback", "feedback"},
            {"📋", "Appraisals", "appraisals"},
            {"🎓", "Skills", "skills"},
            {"🏆", "Rewards", "rewards"},
            {"📉", "Reports", "reports"},
            {"🔗", "Integration", "integration"}
        };

        for (String[] config : navConfigs) {
            JPanel item = createNavItem(config[0], config[1], config[2]);
            navItems.put(config[2], item);
            sidebar.add(item);
            sidebar.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(220, 80));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BOR),
            BorderFactory.createEmptyBorder(14, 20, 18, 20)
        ));

        JLabel footerTitle = new JLabel("OOAD Project");
        footerTitle.setFont(Theme.SANS_XS);
        footerTitle.setForeground(Theme.MU);
        footer.add(footerTitle);

        JLabel footerSub = new JLabel("Performance Mgmt");
        footerSub.setFont(new Font("SansSerif", Font.BOLD, 12));
        footerSub.setForeground(Theme.TX);
        footer.add(Box.createRigidArea(new Dimension(0, 2)));
        footer.add(footerSub);

        sidebar.add(footer);

        add(sidebar, BorderLayout.WEST);
    }

    private JPanel createNavItem(String icon, String label, String id) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 9));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(220, 40));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        iconLbl.setForeground(Theme.MU);
        item.add(iconLbl);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(Theme.MU);
        item.add(lbl);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!currentPage.equals(id)) {
                    item.setOpaque(true);
                    item.setBackground(Theme.SUR2);
                    lbl.setForeground(Theme.TX);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!currentPage.equals(id)) {
                    item.setOpaque(false);
                    lbl.setForeground(Theme.MU);
                }
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showPage(id);
            }
        });

        return item;
    }

    private void initContent() {
        cardLayout = new CardLayout();
        content = new JPanel(cardLayout);
        content.setBackground(Theme.BG);
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        content.add(new DashboardPanel(), "dashboard");
        content.add(new EmployeesPanel(), "employees");
        content.add(new GoalsPanel(), "goals");
        content.add(new KPIPanel(), "kpis");
        content.add(new FeedbackPanel(), "feedback");
        content.add(new AppraisalsPanel(), "appraisals");
        content.add(new SkillsPanel(), "skills");
        content.add(new RewardsPanel(), "rewards");
        content.add(new ReportsPanel(), "reports");
        content.add(new IntegrationPanel(), "integration");

        add(content, BorderLayout.CENTER);
    }

    public void showPage(String id) {
        currentPage = id;
        cardLayout.show(content, id);
        updateNavState();
    }

    private void updateNavState() {
        for (Map.Entry<String, JPanel> entry : navItems.entrySet()) {
            JPanel panel = entry.getValue();
            JLabel icon = (JLabel) panel.getComponent(0);
            JLabel label = (JLabel) panel.getComponent(1);
            
            if (entry.getKey().equals(currentPage)) {
                panel.setOpaque(true);
                panel.setBackground(new Color(232, 201, 109, 30));
                icon.setForeground(Theme.ACC);
                label.setForeground(Theme.ACC);
            } else {
                panel.setOpaque(false);
                icon.setForeground(Theme.MU);
                label.setForeground(Theme.MU);
            }
        }
        sidebar.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PerformanceManagementUI();
        });
    }
}
