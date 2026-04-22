package ui;

import service.PerformanceService;
import models.Appraisal;
import models.Employee;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import interfaces.FormSummary;
import interfaces.WorkflowStepSummary;
import interfaces.WorkflowSummary;
import java.util.List;
import java.util.UUID;

public class AppraisalsPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;

    public AppraisalsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Theme.BG);
        JLabel title = new JLabel("Appraisal Cycles");
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
        
        JLabel sub = new JLabel("Performance appraisal workflows using external review chains");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // New Appraisal
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toolbar.setBackground(Theme.BG);
        JButton addBtn = new JButton("+ New Appraisal");
        addBtn.setBackground(Theme.ACC);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(Theme.SANS_MD);
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.setBorder(null);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddAppraisalDialog());
        toolbar.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Employee", "Cycle", "Manager", "Self", "Peer", "Final", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
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

        refreshTable();

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(Theme.BG);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Appraisal a : service.getAllAppraisals()) {
            Employee emp = service.getEmployeeById(a.getEmpId());
            model.addRow(new Object[]{
                a.getId(),
                emp != null ? emp.getName() : a.getEmpId(),
                a.getCycle(),
                a.getMgr() == null ? "-" : a.getMgr(),
                a.getSelf() == null ? "-" : a.getSelf(),
                a.getPeer() == null ? "-" : a.getPeer(),
                a.getFinalScore() == null ? "-" : a.getFinalScore(),
                a.getStatus()
            });
        }
    }

    private void showAddAppraisalDialog() {
        JDialog dialog = new JDialog((Frame)null, "New Appraisal (Integrated)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.SUR);

        // Integration Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Theme.SUR2);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        FormSummary extForm = service.getExternalForms().stream()
                .filter(f -> f.formId == 102).findFirst().orElse(null);
        WorkflowSummary extWorkflow = service.getExternalWorkflows().stream()
                .filter(w -> w.workflowId == 202).findFirst().orElse(null);
        
        if (extForm != null) {
            JLabel formLabel = new JLabel("● Form: " + extForm.formName + " (" + extForm.layoutType + ")");
            formLabel.setFont(Theme.SANS_XS);
            formLabel.setForeground(Theme.ACC);
            infoPanel.add(formLabel);
        }
        
        if (extWorkflow != null) {
            JLabel wfLabel = new JLabel("● Workflow: " + extWorkflow.workflowName);
            wfLabel.setFont(Theme.SANS_XS);
            wfLabel.setForeground(Theme.MU);
            infoPanel.add(wfLabel);
            
            List<WorkflowStepSummary> steps = service.getExternalWorkflowSteps(extWorkflow.workflowId);
            for (WorkflowStepSummary step : steps) {
                JLabel stepLabel = new JLabel("  - " + step.stepName + " -> " + step.assignee + " (" + step.escalationHours + "h SLA)");
                stepLabel.setFont(Theme.SANS_XS);
                stepLabel.setForeground(Theme.MU);
                infoPanel.add(stepLabel);
            }
        }
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> empList = new JComboBox<>();
        for (Employee e : service.getAllEmployees()) empList.addItem(e.getEmployeeId() + " - " + e.getName());
        
        JTextField cycle = new JTextField("2025 Annual Review");
        JTextField mgr = new JTextField();
        JTextField self = new JTextField();
        JTextField peer = new JTextField();
        JComboBox<String> status = new JComboBox<>(new String[]{"pending", "in-progress", "completed"});

        addLabel(form, "Employee");
        form.add(empList);
        addLabel(form, "Performance Cycle");
        form.add(createField(cycle));
        addLabel(form, "Manager Score (0-100)");
        form.add(createField(mgr));
        addLabel(form, "Self Score (0-100)");
        form.add(createField(self));
        addLabel(form, "Peer Score (0-100)");
        form.add(createField(peer));
        addLabel(form, "Status");
        form.add(status);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save & Route for Approval");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);

        save.addActionListener(e -> {
            try {
                Appraisal a = new Appraisal();
                a.setId("A" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
                a.setEmpId(empList.getSelectedItem().toString().split(" - ")[0]);
                a.setCycle(cycle.getText());
                a.setMgr(mgr.getText().isEmpty() ? null : Integer.parseInt(mgr.getText()));
                a.setSelf(self.getText().isEmpty() ? null : Integer.parseInt(self.getText()));
                a.setPeer(peer.getText().isEmpty() ? null : Integer.parseInt(peer.getText()));
                
                if (a.getMgr() != null && a.getSelf() != null && a.getPeer() != null) {
                    a.setFinalScore((a.getMgr() + a.getSelf() + a.getPeer()) / 3);
                }
                
                a.setStatus(status.getSelectedItem().toString());
                a.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                
                service.addAppraisal(a);
                refreshTable();
                
                if (extWorkflow != null) {
                    JOptionPane.showMessageDialog(dialog, "Appraisal route to: " + extWorkflow.assignedTo);
                }
                
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Scores must be valid numbers");
            }
        });

        cancel.addActionListener(ev -> dialog.dispose());
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
