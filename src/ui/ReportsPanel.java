package ui;

import service.PerformanceService;
import service.DatabaseManager;
import models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 24));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("Analytics & Compliance");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Audit trails, succession planning and performance trends");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Grid for reports
        JPanel grid = new JPanel(new GridLayout(3, 2, 20, 20));
        grid.setBackground(Theme.BG);

        grid.add(createReportCard("Succession Pipeline", "Identify leadership readiness and career paths.", "View Pipeline"));
        grid.add(createReportCard("Audit Trail", "View all changes for compliance and transparency.", "View Logs"));
        grid.add(createReportCard("Trend Analysis", "Compare performance across departments and cycles.", "View Trends"));
        grid.add(createReportCard("Learning Impact", "Track how training improved performance scores.", "View Impact"));
        grid.add(createReportCard("External Subsystem Audit", "Integrated report generated via Customization subsystem.", "Generate External Report"));

        add(grid, BorderLayout.CENTER);
    }

    private JPanel createReportCard(String title, String desc, String btnText) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.SUR);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(title);
        t.setFont(Theme.SANS_LG);
        t.setForeground(Theme.TX);
        
        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(Theme.SANS_MD);
        d.setForeground(Theme.MU);

        JButton btn = new JButton(btnText);
        btn.setBackground(Theme.SUR2);
        btn.setForeground(Theme.TX);
        btn.setFont(Theme.SANS_SM);
        btn.addActionListener(e -> handleReportClick(title));

        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }

    private void handleReportClick(String reportName) {
        if ("Audit Trail".equals(reportName)) {
            showAuditTrail();
        } else if ("Succession Pipeline".equals(reportName)) {
            showSuccessionPipeline();
        } else if ("Trend Analysis".equals(reportName)) {
            showTrendAnalysis();
        } else if ("Learning Impact".equals(reportName)) {
            showLearningImpact();
        } else if ("External Subsystem Audit".equals(reportName)) {
            showExternalReport();
        } else {
            JOptionPane.showMessageDialog(this, "Generating " + reportName + "...", "Report", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showExternalReport() {
        String report = service.generateExternalReport("Subsystem Integration Check");
        JTextArea area = new JTextArea(report);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(Theme.SUR);
        area.setForeground(Theme.TX);
        area.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scroll, "External Integration Report", JOptionPane.PLAIN_MESSAGE);
    }

    private void showSuccessionPipeline() {
        JDialog dialog = new JDialog((Frame)null, "Succession Pipeline (High Potential)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Name", "Dept", "Score", "Readiness"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);

        for (Employee e : service.getAllEmployees()) {
            if (e.getScore() >= 85) {
                model.addRow(new Object[]{e.getName(), e.getDept(), e.getScore(), "Ready Now"});
            } else if (e.getScore() >= 75) {
                model.addRow(new Object[]{e.getName(), e.getDept(), e.getScore(), "1-2 Years"});
            }
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showTrendAnalysis() {
        JDialog dialog = new JDialog((Frame)null, "Trend Analysis (Avg Score by Dept)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Department", "Average Score"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dept, AVG(score) as avg_score FROM employees GROUP BY dept")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("dept"),
                    String.format("%.2f", rs.getDouble("avg_score"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showLearningImpact() {
        JDialog dialog = new JDialog((Frame)null, "Learning Impact (Training Progress)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Employee", "Course", "Priority", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);

        for (TrainingSuggestion ts : service.getAllTrainingSuggestions()) {
            model.addRow(new Object[]{
                ts.getEmpId(),
                ts.getCourseName(),
                ts.getPriority(),
                ts.getStatus()
            });
        }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showAuditTrail() {
        JDialog dialog = new JDialog((Frame)null, "Audit Trail", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Action", "Entity", "ID", "Details", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("action"),
                    rs.getString("entityType"),
                    rs.getString("entityId"),
                    rs.getString("details"),
                    rs.getString("timestamp")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
