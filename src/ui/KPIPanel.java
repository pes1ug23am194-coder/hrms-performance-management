package ui;

import service.PerformanceService;
import models.KPI;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class KPIPanel extends JPanel {
    private PerformanceService service = PerformanceService.getInstance();
    private JPanel grid;

    public KPIPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("KPI Management");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Key performance indicators by department");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 24)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // KPI Grid
        grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setBackground(Theme.BG);

        refreshGrid();

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private void refreshGrid() {
        grid.removeAll();
        List<KPI> kpis = service.getAllKPIs();
        System.out.println("DEBUG: Found " + kpis.size() + " KPIs in database.");
        for (KPI k : kpis) {
            grid.add(createKPICard(k));
        }
        
        if (kpis.isEmpty()) {
            JLabel empty = new JLabel("No KPI data found in hrms.db");
            empty.setForeground(Theme.MU);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            grid.add(empty);
        }
        
        grid.revalidate();
        grid.repaint();
    }

    private JPanel createKPICard(KPI k) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.SUR);
        int p = (int) Math.min(100, Math.round((k.getCurrent() / k.getTarget()) * 100));
        Color color = p >= 90 ? Theme.GR : p >= 70 ? Theme.OR : Theme.RD;
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, color),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel dept = new JLabel(k.getDept().toUpperCase());
        dept.setFont(Theme.SANS_XS);
        dept.setForeground(Theme.MU);
        card.add(dept);

        JLabel name = new JLabel(k.getName());
        name.setFont(Theme.SANS_MD);
        name.setForeground(Theme.TX);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(name);

        JLabel val = new JLabel(k.getCurrent() + k.getUnit());
        val.setFont(Theme.SERIF_LG);
        val.setForeground(Theme.TX);
        card.add(Box.createRigidArea(new Dimension(0, 3)));
        card.add(val);

        JLabel target = new JLabel("Target: " + k.getTarget() + k.getUnit());
        target.setFont(Theme.SANS_SM);
        target.setForeground(Theme.MU);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(target);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(p);
        progress.setBackground(Theme.BOR);
        progress.setForeground(color);
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(0, 4));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(progress);

        JButton editBtn = new JButton("Update Value");
        editBtn.setBackground(Theme.SUR2);
        editBtn.setForeground(Theme.ACC);
        editBtn.setFont(Theme.SANS_XS);
        editBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        editBtn.addActionListener(e -> showEditKPIDialog(k));
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(editBtn);

        return card;
    }

    private void showEditKPIDialog(KPI k) {
        String input = JOptionPane.showInputDialog(this, "Enter current value for " + k.getName(), k.getCurrent());
        if (input != null && !input.isEmpty()) {
            try {
                k.setCurrent(Double.parseDouble(input));
                service.updateKPI(k);
                refreshGrid();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }
}
