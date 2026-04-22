package ui;

import service.PerformanceService;
import service.DatabaseManager;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillsPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;

    public SkillsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("Skills & Competencies");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Assess employee skills and identify training gaps");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(Theme.BG);
        JButton gapBtn = new JButton("Skill Gap Analysis");
        gapBtn.setBackground(Theme.SUR2);
        gapBtn.setForeground(Theme.TX);
        gapBtn.addActionListener(e -> showGapAnalysis());
        toolbar.add(gapBtn);

        JButton suggestBtn = new JButton("+ Suggest Training");
        suggestBtn.setBackground(Theme.ACC);
        suggestBtn.setForeground(Color.BLACK);
        suggestBtn.addActionListener(e -> showSuggestTrainingDialog());
        toolbar.add(suggestBtn);

        // Table
        String[] cols = {"Employee", "Skill", "Current Level", "Target Level", "Gap", "Action"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        table = new JTable(model);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    String employeeName = (String) model.getValueAt(row, 0);
                    String skillName = (String) model.getValueAt(row, 1);
                    String action = (String) model.getValueAt(row, 5);
                    if ("Assign Training".equals(action)) {
                        showSuggestTrainingDialog(employeeName, skillName);
                    }
                }
            }
        });

        refreshTable();

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(Theme.BG);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);
        table.setGridColor(Theme.BOR);
        table.setFont(Theme.SANS_MD);
        table.getTableHeader().setBackground(Theme.SUR);
        table.getTableHeader().setForeground(Theme.MU);
        table.getTableHeader().setFont(Theme.SANS_XS);
        table.setRowHeight(50);
    }

    private void refreshTable() {
        model.setRowCount(0);
        String sql = "SELECT e.name, s.name as skill, es.currentLevel, es.targetLevel " +
                     "FROM employee_skills es " +
                     "JOIN employees e ON es.employeeId = e.employeeId " +
                     "JOIN skills s ON es.skillId = s.skillId";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int current = rs.getInt("currentLevel");
                int target = rs.getInt("targetLevel");
                int gap = target - current;
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("skill"),
                    current + "/5",
                    target + "/5",
                    gap > 0 ? gap + " levels" : "Met",
                    gap > 0 ? "Assign Training" : "Keep Up"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showGapAnalysis() {
        StringBuilder sb = new StringBuilder("Skill & Competency Analysis:\n\n");
        
        // Skill gaps
        sb.append("--- Skill Gaps ---\n");
        String skillSql = "SELECT e.name, s.name as skill, es.currentLevel, es.targetLevel " +
                     "FROM employee_skills es " +
                     "JOIN employees e ON es.employeeId = e.employeeId " +
                     "JOIN skills s ON es.skillId = s.skillId " +
                     "WHERE es.targetLevel > es.currentLevel";
                     
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(skillSql)) {
            
            while (rs.next()) {
                sb.append(rs.getString("name")).append(" - ")
                  .append(rs.getString("skill")).append(": ")
                  .append(rs.getInt("currentLevel")).append(" -> ")
                  .append(rs.getInt("targetLevel")).append("\n");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Competencies list
        sb.append("\n--- Core Competencies ---\n");
        String compSql = "SELECT name, category FROM competencies";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(compSql)) {
            while (rs.next()) {
                sb.append(rs.getString("name")).append(" (").append(rs.getString("category")).append(")\n");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Skills & Competencies Analysis", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuggestTrainingDialog() {
        showSuggestTrainingDialog("", "");
    }

    private void showSuggestTrainingDialog(String employeeName, String skillName) {
        JDialog dialog = new JDialog((Frame)null, "Suggest Training", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField empField = new JTextField(employeeName);
        JTextField skillField = new JTextField(skillName);
        JTextField courseField = new JTextField();
        JComboBox<String> priority = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        addFormField(form, "Employee Name/ID", empField);
        addFormField(form, "Skill", skillField);
        addFormField(form, "Recommended Course", courseField);
        
        JLabel l = new JLabel("Priority");
        l.setForeground(Theme.MU);
        l.setFont(Theme.SANS_XS);
        form.add(l);
        form.add(priority);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Suggest");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);
        
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            TrainingSuggestion ts = new TrainingSuggestion();
            ts.setId("T" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            ts.setEmpId(empField.getText());
            ts.setSkillName(skillField.getText());
            ts.setCourseName(courseField.getText());
            ts.setPriority((String)priority.getSelectedItem());
            ts.setStatus("Suggested");
            
            service.addTrainingSuggestion(ts);
            JOptionPane.showMessageDialog(dialog, "Training suggested successfully!");
            dialog.dispose();
        });

        actions.add(cancel);
        actions.add(save);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel p, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MU);
        l.setFont(Theme.SANS_XS);
        p.add(l);
        field.setBackground(Theme.SUR2);
        field.setForeground(Theme.TX);
        field.setCaretColor(Theme.TX);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        p.add(field);
    }
}
