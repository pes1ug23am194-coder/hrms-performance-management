package ui;

import service.PerformanceService;
import models.Feedback;
import models.Employee;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import interfaces.FieldSummary;
import interfaces.FormSummary;
import java.util.List;
import java.util.UUID;

public class FeedbackPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;

    public FeedbackPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Theme.BG);
        JLabel title = new JLabel("360° Feedback");
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
        
        JLabel sub = new JLabel("Multi-source feedback collection using custom external forms");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Submit Feedback
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toolbar.setBackground(Theme.BG);
        JButton addBtn = new JButton("+ Submit Feedback");
        addBtn.setBackground(Theme.ACC);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(Theme.SANS_MD);
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.setBorder(null);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showSubmitFeedbackDialog());
        toolbar.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "For", "From", "Type", "Category", "Feedback", "Date", "Actions"};
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
                    if (JOptionPane.showConfirmDialog(null, "Delete feedback?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        service.deleteFeedback(id);
                        refreshTable();
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

    private void refreshTable() {
        model.setRowCount(0);
        for (Feedback f : service.getAllFeedback()) {
            Employee emp = service.getEmployeeById(f.getEmpId());
            model.addRow(new Object[]{
                f.getId(),
                emp != null ? emp.getName() : f.getEmpId(),
                f.getFrom(),
                f.getType(),
                f.getCategory(),
                f.getText(),
                f.getDate(),
                "✕"
            });
        }
    }

    private void showSubmitFeedbackDialog() {
        JDialog dialog = new JDialog((Frame)null, "Submit Feedback (Integrated)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.SUR);

        // Integration Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Theme.SUR2);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        FormSummary extForm = service.getExternalForms().stream()
                .filter(f -> f.formId == 103).findFirst().orElse(null);
        
        if (extForm != null) {
            JLabel formLabel = new JLabel("● Using External Form: " + extForm.formName + " (" + extForm.layoutType + ")");
            formLabel.setFont(Theme.SANS_XS);
            formLabel.setForeground(Theme.ACC);
            infoPanel.add(formLabel);
        }
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> empList = new JComboBox<>();
        for (Employee e : service.getAllEmployees()) empList.addItem(e.getEmployeeId() + " - " + e.getName());
        
        JTextField from = new JTextField();
        JComboBox<String> type = new JComboBox<>(new String[]{"positive", "improvement"});
        JComboBox<String> category = new JComboBox<>(new String[]{"Technical", "Communication", "Leadership", "Teamwork", "Delivery"});
        JTextArea text = new JTextArea(4, 20);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        // Dynamic fields from external form
        List<FieldSummary> extFields = service.getExternalFieldsForForm(103);

        addLabel(form, "For Employee");
        form.add(empList);
        addLabel(form, "From (Reviewer Name)");
        form.add(createField(from));
        addLabel(form, "Type");
        form.add(type);
        addLabel(form, "Category");
        form.add(category);
        
        for (FieldSummary field : extFields) {
            if (field.fieldName.equalsIgnoreCase("Comment")) {
                addLabel(form, "External Feedback Comment" + (field.isMandatory ? " *" : ""));
                JScrollPane textScroll = new JScrollPane(text);
                textScroll.setBorder(BorderFactory.createLineBorder(Theme.BOR));
                text.setBackground(Theme.SUR2);
                text.setForeground(Theme.TX);
                text.setCaretColor(Theme.TX);
                form.add(textScroll);
            }
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Submit External Form");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);

        save.addActionListener(e -> {
            if (from.getText().isEmpty() || text.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Reviewer and Feedback are required");
                return;
            }
            Feedback f = new Feedback();
            f.setId("F" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            f.setEmpId(empList.getSelectedItem().toString().split(" - ")[0]);
            f.setFrom(from.getText());
            f.setType(type.getSelectedItem().toString());
            f.setCategory(category.getSelectedItem().toString());
            f.setText(text.getText());
            f.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            
            service.addFeedback(f);
            refreshTable();
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
