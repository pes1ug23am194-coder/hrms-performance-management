package ui;

import service.PerformanceService;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("Performance Management");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("HRMS — Real-time Live Data");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 28)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statsGrid.setBackground(Theme.BG);
        statsGrid.add(createStatCard("Avg Performance", String.valueOf(service.getAverageScore()), service.getAllEmployees().size() + " employees", Theme.ACC));
        statsGrid.add(createStatCard("Goals On Track", service.getGoalsOnTrackPercentage() + "%", service.getGoalsOnTrackCount() + "/" + service.getAllGoals().size() + " goals", Theme.GR));
        statsGrid.add(createStatCard("Total Feedback", String.valueOf(service.getAllFeedback().size()), "All cycles", Theme.BL));
        statsGrid.add(createStatCard("Employees", String.valueOf(service.getAllEmployees().size()), "Active records", Theme.RD));

        // Main content
        JPanel main = new JPanel(new GridLayout(1, 2, 18, 0));
        main.setBackground(Theme.BG);
        main.add(createLeaderboardPanel());
        main.add(createKPIPanel());

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG);
        center.add(statsGrid, BorderLayout.NORTH);
        center.add(Box.createRigidArea(new Dimension(0, 24)), BorderLayout.CENTER);
        center.add(main, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String label, String value, String change, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.SUR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, color),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.SANS_XS);
        lbl.setForeground(Theme.MU);
        card.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(Theme.SERIF_LG);
        val.setForeground(Theme.TX);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(val);

        JLabel chg = new JLabel(change);
        chg.setFont(Theme.SANS_SM);
        chg.setForeground(Theme.GR);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(chg);

        return card;
    }

    private JPanel createLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.SUR);
        panel.setBorder(BorderFactory.createLineBorder(Theme.BOR));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SUR);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 13, 20));
        JLabel title = new JLabel("Performance Leaderboard");
        title.setFont(Theme.SANS_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Theme.SUR);
        list.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

        List<Employee> emps = service.getAllEmployees();
        emps.sort((a, b) -> b.getScore() - a.getScore());

        for (Employee e : emps) {
            list.add(createEmployeeRow(e));
            list.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmployeeRow(Employee e) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Theme.SUR2);
        row.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel avatar = new JLabel(e.getAvatar(), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setBackground(Theme.BL);
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setFont(Theme.SANS_XS);
        row.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Theme.SUR2);
        JLabel name = new JLabel(e.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 13));
        name.setForeground(Theme.TX);
        JLabel role = new JLabel(e.getRole());
        role.setFont(Theme.SANS_SM);
        role.setForeground(Theme.MU);
        info.add(name);
        info.add(role);
        row.add(info, BorderLayout.CENTER);

        JLabel score = new JLabel(String.valueOf(e.getScore()));
        score.setFont(new Font("SansSerif", Font.BOLD, 13));
        score.setForeground(e.getScore() >= 80 ? Theme.GR : Theme.OR);
        row.add(score, BorderLayout.EAST);

        return row;
    }

    private JPanel createKPIPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.SUR);
        panel.setBorder(BorderFactory.createLineBorder(Theme.BOR));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SUR);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 13, 20));
        JLabel title = new JLabel("KPI Overview");
        title.setFont(Theme.SANS_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"KPI", "Dept", "Progress"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (KPI k : service.getAllKPIs()) {
            int progress = (int) Math.min(100, Math.round((k.getCurrent() / k.getTarget()) * 100));
            model.addRow(new Object[]{k.getName(), k.getDept(), progress + "%"});
        }

        JTable table = new JTable(model);
        table.setBackground(Theme.SUR);
        table.setForeground(Theme.TX);
        table.setGridColor(Theme.BOR);
        table.setFont(Theme.SANS_MD);
        table.getTableHeader().setBackground(Theme.SUR);
        table.getTableHeader().setForeground(Theme.MU);
        table.getTableHeader().setFont(Theme.SANS_XS);
        table.setRowHeight(40);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
