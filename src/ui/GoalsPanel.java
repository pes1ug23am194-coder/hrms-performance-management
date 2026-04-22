package ui;

import service.PerformanceService;
import models.Goal;
import models.Employee;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import interfaces.FieldSummary;
import interfaces.FormSummary;
import interfaces.WorkflowSummary;
import java.util.List;
import java.util.UUID;

public class GoalsPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;

    public GoalsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Theme.BG);
        JLabel title = new JLabel("Goal Tracking");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        titlePanel.add(title);
        
        // Integration Badge
        JLabel badge = new JLabel(" INTEGRATED WITH HRMS ");
        badge.setFont(Theme.SANS_XS);
        badge.setForeground(Color.BLACK);
        badge.setOpaque(true);
        badge.setBackground(Theme.ACC);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(badge);
        
        header.add(titlePanel, BorderLayout.NORTH);
        
        JLabel sub = new JLabel("Set, monitor and update employee goals using external forms & workflows");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Filter and Add
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Theme.BG);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setBackground(Theme.BG);
        String[] opts = {"All", "on-track", "at-risk", "delayed", "completed"};
        for (String opt : opts) {
            JButton btn = new JButton(opt.equals("All") ? "All" : opt.substring(0,1).toUpperCase() + opt.substring(1));
            btn.setBackground(Theme.SUR2);
            btn.setForeground(Theme.TX);
            btn.setFont(Theme.SANS_SM);
            btn.addActionListener(e -> refreshTable(opt.equals("All") ? "" : opt));
            filters.add(btn);
        }
        toolbar.add(filters, BorderLayout.CENTER);

        JButton addBtn = new JButton("+ New Goal");
        addBtn.setBackground(Theme.ACC);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(Theme.SANS_MD);
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.setBorder(null);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddGoalDialog());
        toolbar.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Goal", "Employee", "Deadline", "Priority", "Status", "Progress", "Actions"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 7; }
        };

        table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);
        table.setGridColor(Theme.BOR);
        table.setFont(Theme.SANS_MD);
        table.getTableHeader().setBackground(Theme.SUR);
        table.getTableHeader().setForeground(Theme.MU);
        table.getTableHeader().setFont(Theme.SANS_XS);
        table.setRowHeight(50);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 7) {
                    String id = (String) model.getValueAt(row, 0);
                    if (JOptionPane.showConfirmDialog(null, "Delete goal?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        service.deleteGoal(id);
                        refreshTable("");
                    }
                }
            }
        });

        refreshTable("");

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(Theme.BG);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void refreshTable(String statusFilter) {
        model.setRowCount(0);
        List<Goal> goals = service.getAllGoals();
        if (goals.isEmpty()) {
            // No goals found, might need to re-seed or check database
        }
        for (Goal g : goals) {
            if (statusFilter.isEmpty() || g.getStatus().equals(statusFilter)) {
                Employee emp = service.getEmployeeById(g.getEmployeeId());
                model.addRow(new Object[]{
                    g.getGoalId(),
                    g.getTitle(),
                    emp != null ? emp.getName() : g.getEmployeeId(),
                    g.getDeadline(),
                    g.getPriority(),
                    g.getStatus(),
                    g.getProgress() + "%",
                    "✕"
                });
            }
        }
    }

    private void showAddGoalDialog() {
        JDialog dialog = new JDialog((Frame)null, "New Goal (Integrated)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.SUR);

        // Integration Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Theme.SUR2);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        FormSummary extForm = service.getExternalForms().stream()
                .filter(f -> f.formId == 101).findFirst().orElse(null);
        WorkflowSummary extWorkflow = service.getExternalWorkflows().stream()
                .filter(w -> w.workflowId == 201).findFirst().orElse(null);
        
        if (extForm != null || extWorkflow != null) {
            JLabel infoTitle = new JLabel("INTEGRATION STATUS");
            infoTitle.setFont(Theme.SANS_XS);
            infoTitle.setForeground(Theme.MU);
            infoPanel.add(infoTitle);
        }

        if (extForm != null) {
            JLabel formLabel = new JLabel("● Form: " + extForm.formName + " (" + extForm.layoutType + ")");
            formLabel.setFont(Theme.SANS_XS);
            formLabel.setForeground(Theme.ACC);
            infoPanel.add(formLabel);
        }
        
        if (extWorkflow != null) {
            JLabel wfLabel = new JLabel("● Workflow: " + extWorkflow.workflowName + " (" + extWorkflow.currentStatus + ")");
            wfLabel.setFont(Theme.SANS_XS);
            wfLabel.setForeground(Theme.MU);
            infoPanel.add(wfLabel);
        }
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> empList = new JComboBox<>();
        for (Employee e : service.getAllEmployees()) empList.addItem(e.getEmployeeId() + " - " + e.getName());
        
        JTextField title = new JTextField();
        JTextField deadline = new JTextField("2025-12-31");
        JComboBox<String> priority = new JComboBox<>(new String[]{"low", "medium", "high", "critical"});
        priority.setSelectedItem("high");

        // Dynamic fields from external form
        List<FieldSummary> extFields = service.getExternalFieldsForForm(101);
        
        addLabel(form, "Employee");
        form.add(empList);
        
        for (FieldSummary field : extFields) {
            addLabel(form, field.fieldName + (field.isMandatory ? " *" : ""));
            if (field.fieldName.equalsIgnoreCase("Goal Title")) {
                form.add(createField(title));
            } else if (field.fieldName.equalsIgnoreCase("Deadline")) {
                form.add(createField(deadline));
            } else {
                form.add(createField(new JTextField()));
            }
        }
        
        addLabel(form, "Priority");
        form.add(priority);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save & Trigger Workflow");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);

        save.addActionListener(e -> {
            if (title.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title is required");
                return;
            }
            Goal g = new Goal();
            g.setGoalId("G" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            g.setEmployeeId(empList.getSelectedItem().toString().split(" - ")[0]);
            g.setTitle(title.getText());
            g.setDeadline(deadline.getText());
            g.setPriority(priority.getSelectedItem().toString());
            g.setStatus("on-track");
            g.setProgress(0);
            
            service.addGoal(g);
            refreshTable("");
            
            // Simulation of workflow trigger
            if (extWorkflow != null) {
                JOptionPane.showMessageDialog(dialog, "Goal added and '" + extWorkflow.workflowName + "' triggered!");
            }
            
            dialog.dispose();
        });

        cancel.addActionListener(e -> dialog.dispose());
        actions.add(cancel);
        actions.add(save);

        mainPanel.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.MU);
        l.setFont(Theme.SANS_XS);
        p.add(l);
    }

    private JTextField createField(JTextField f) {
        f.setBackground(Theme.SUR2);
        f.setForeground(Theme.TX);
        f.setCaretColor(Theme.TX);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return f;
    }
}
