package ui;

import service.PerformanceService;
import service.DatabaseManager;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class RewardsPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private DefaultTableModel model;
    private JTable table;

    public RewardsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("Rewards & Recognition");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Link performance to bonuses, promotions and awards");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Stats
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setBackground(Theme.BG);
        stats.add(createMiniCard("High Performers", "3", Theme.GR));
        stats.add(createMiniCard("Total Bonuses", "$12,400", Theme.BL));
        stats.add(createMiniCard("Pending Rewards", "2", Theme.OR));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(Theme.BG);
        JButton addBtn = new JButton("+ Add Reward");
        addBtn.setBackground(Theme.ACC);
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e -> showAddRewardDialog());
        toolbar.add(addBtn);

        // Table
        String[] cols = {"ID", "Employee ID", "Type", "Amount", "Reason", "Date", "Action"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        table = new JTable(model);
        styleTable(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 6) {
                    String id = (String) model.getValueAt(row, 0);
                    if (JOptionPane.showConfirmDialog(null, "Delete Reward " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        service.deleteReward(id);
                        refreshTable();
                    }
                }
            }
        });

        refreshTable();

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(Theme.BG);
        center.add(stats, BorderLayout.NORTH);
        
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setBackground(Theme.BG);
        tableContainer.add(toolbar, BorderLayout.NORTH);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        
        center.add(tableContainer, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Reward r : service.getAllRewards()) {
            model.addRow(new Object[]{
                r.getRewardId(),
                r.getEmpId(),
                r.getType(),
                "$" + String.format("%.2f", r.getAmount()),
                r.getReason(),
                r.getDate(),
                "Delete"
            });
        }
    }

    private void showAddRewardDialog() {
        JDialog dialog = new JDialog((Frame)null, "Add Reward", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(Theme.SUR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField empId = new JTextField();
        JTextField type = new JTextField();
        JTextField amount = new JTextField();
        JTextField reason = new JTextField();
        JTextField date = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        addFormField(form, "Employee ID", empId);
        addFormField(form, "Type (Bonus/Promotion/Award)", type);
        addFormField(form, "Amount", amount);
        addFormField(form, "Reason", reason);
        addFormField(form, "Date (YYYY-MM-DD)", date);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Theme.SUR);
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");
        save.setBackground(Theme.ACC);
        save.setForeground(Color.BLACK);
        
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            try {
                Reward r = new Reward();
                r.setRewardId("R" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase());
                r.setEmpId(empId.getText());
                r.setType(type.getText());
                r.setAmount(Double.parseDouble(amount.getText()));
                r.setReason(reason.getText());
                r.setDate(date.getText());
                
                service.addReward(r);
                refreshTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
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

    private JPanel createMiniCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SUR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.SANS_XS);
        lbl.setForeground(Theme.MU);
        JLabel val = new JLabel(value);
        val.setFont(Theme.SERIF_MD);
        val.setForeground(Theme.TX);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
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
}
