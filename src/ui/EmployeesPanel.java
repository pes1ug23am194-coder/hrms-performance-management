package ui;

import service.PerformanceService;
import models.Employee;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class EmployeesPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;

    public EmployeesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("All Employees");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("View, add and edit employee records");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Search and Add
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Theme.BG);
        searchField = new JTextField();
        searchField.setBackground(Theme.SUR2);
        searchField.setForeground(Theme.TX);
        searchField.setCaretColor(Theme.TX);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BOR),
            BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshTable(searchField.getText());
            }
        });
        toolbar.add(searchField, BorderLayout.CENTER);

        JButton addBtn = new JButton("+ Add Employee");
        addBtn.setBackground(Theme.ACC);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(Theme.SANS_MD);
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.setBorder(null);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddEmployeeDialog());
        toolbar.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Name", "Role", "Dept", "Score", "Trend", "Manager", "Actions"};
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

        // Custom renderer for actions
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
                p.setBackground(isSelected ? Theme.SUR2 : Theme.SUR);
                JButton edit = new JButton("Edit");
                edit.setBackground(Theme.SUR2);
                edit.setForeground(Theme.ACC);
                edit.setFont(Theme.SANS_XS);
                JButton del = new JButton("X");
                del.setBackground(Theme.SUR2);
                del.setForeground(Theme.RD);
                del.setFont(Theme.SANS_XS);
                p.add(edit);
                p.add(del);
                return p;
            }
        });

        // Add action listener to table
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 7) {
                    String id = (String) model.getValueAt(row, 0);
                    Employee emp = service.getEmployeeById(id);
                    if (e.getX() % table.getColumnModel().getColumn(7).getWidth() < table.getColumnModel().getColumn(7).getWidth() / 2) {
                        showEditEmployeeDialog(emp);
                    } else {
                        if (JOptionPane.showConfirmDialog(null, "Delete " + emp.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            service.deleteEmployee(id);
                            refreshTable("");
                        }
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

    private void refreshTable(String search) {
        model.setRowCount(0);
        for (Employee e : service.getAllEmployees()) {
            if (search.isEmpty() || e.getName().toLowerCase().contains(search.toLowerCase()) || e.getDept().toLowerCase().contains(search.toLowerCase())) {
                model.addRow(new Object[]{
                    e.getEmployeeId(),
                    e.getName(),
                    e.getRole(),
                    e.getDept(),
                    e.getScore(),
                    e.getTrend(),
                    e.getManager(),
                    "Edit / Delete"
                });
            }
        }
    }

    private void showAddEmployeeDialog() {
        showEmployeeDialog(null);
    }

    private void showEditEmployeeDialog(Employee emp) {
        showEmployeeDialog(emp);
    }

    private void showEmployeeDialog(Employee existing) {
        JDialog dialog = new JDialog((Frame)null, existing == null ? "Add Employee" : "Edit Employee", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField name = new JTextField(existing != null ? existing.getName() : "");
        JTextField role = new JTextField(existing != null ? existing.getRole() : "");
        JTextField dept = new JTextField(existing != null ? existing.getDept() : "");
        JTextField email = new JTextField(existing != null ? existing.getEmail() : "");
        JTextField manager = new JTextField(existing != null ? existing.getManager() : "");
        JTextField score = new JTextField(existing != null ? String.valueOf(existing.getScore()) : "70");

        addFormField(form, "Name", name);
        addFormField(form, "Role", role);
        addFormField(form, "Department", dept);
        addFormField(form, "Email", email);
        addFormField(form, "Manager", manager);
        addFormField(form, "Score (0-100)", score);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);
        
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            if (name.getText().isEmpty() || role.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Role are required");
                return;
            }
            Employee emp = existing != null ? existing : new Employee();
            if (existing == null) {
                emp.setEmployeeId("E" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
                emp.setTrend("+0%");
                emp.setJoinDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
                emp.setAvatar(name.getText().substring(0, Math.min(2, name.getText().length())).toUpperCase());
            }
            emp.setName(name.getText());
            emp.setRole(role.getText());
            emp.setDept(dept.getText());
            emp.setEmail(email.getText());
            emp.setManager(manager.getText());
            try {
                emp.setScore(Integer.parseInt(score.getText()));
            } catch (Exception ex) { emp.setScore(70); }

            if (existing == null) service.addEmployee(emp);
            else service.updateEmployee(emp);
            
            refreshTable("");
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
