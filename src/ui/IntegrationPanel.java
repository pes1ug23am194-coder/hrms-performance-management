package ui;

import interfaces.*;
import service.IntegrationBus;
import service.PerformanceService;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class IntegrationPanel extends JPanel {
    private IntegrationBus bus = IntegrationBus.getInstance();
    private PerformanceService service = PerformanceService.getInstance();
    private JTextArea logArea;
    private JTextArea statusArea;

    public IntegrationPanel() {
        setLayout(new BorderLayout(0, 24));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Register mock systems that represent OTHER databases
        if (!bus.isRegistered("HR_Database_Subsystem")) {
            Map<String, Function<Object[], Object>> methods = new HashMap<>();
            methods.put("fetchSalaryData", args -> "Fetched Salary for " + args[0] + " from HR SQL Database: $5000");
            methods.put("fetchAttendance", args -> "Attendance for " + args[0] + ": 98%");
            bus.register("HR_Database_Subsystem", methods);
        }

        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel title = new JLabel("Integration Hub");
        title.setFont(Theme.SERIF_LG);
        title.setForeground(Theme.TX);
        header.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Connect with any HRMS subsystem via the Integration Bus");
        sub.setFont(Theme.SANS_SM);
        sub.setForeground(Theme.MU);
        header.add(sub, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(0, 16)), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Main Content Split
        JPanel main = new JPanel(new GridLayout(1, 2, 24, 0));
        main.setBackground(Theme.BG);

        // Left Side: Active Systems & Actions
        JPanel left = new JPanel(new BorderLayout(0, 16));
        left.setBackground(Theme.BG);
        
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(Theme.SUR);
        actions.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel sysTitle = new JLabel("INTEGRATION BUS CONTROLS");
        sysTitle.setFont(Theme.SANS_XS);
        sysTitle.setForeground(Theme.ACC);
        actions.add(sysTitle);
        actions.add(Box.createRigidArea(new Dimension(0, 15)));

        // Registration simulation button
        JButton regBtn = new JButton("+ Integrate New External System");
        regBtn.setBackground(Theme.SUR2);
        regBtn.setForeground(Theme.TX);
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.addActionListener(e -> showRegisterDialog());
        actions.add(regBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel callTitle = new JLabel("CROSS-DATABASE DATA SHARING (SIMULATION)");
        callTitle.setFont(Theme.SANS_XS);
        callTitle.setForeground(Theme.MU);
        actions.add(callTitle);
        actions.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton salaryBtn = new JButton("Fetch External Salary Data (E001)");
        salaryBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        salaryBtn.addActionListener(e -> {
            Object res = bus.call("HR_Database_Subsystem", "fetchSalaryData", "E001");
            JOptionPane.showMessageDialog(this, "External DB Result: " + res);
        });
        actions.add(salaryBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton attendanceBtn = new JButton("Fetch External Attendance (E001)");
        attendanceBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        attendanceBtn.addActionListener(e -> {
            Object res = bus.call("HR_Database_Subsystem", "fetchAttendance", "E001");
            JOptionPane.showMessageDialog(this, "External DB Result: " + res);
        });
        actions.add(attendanceBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 20)));

        // New integration with Customization Subsystem
        JLabel extTitle = new JLabel("INTEGRATION WITH CUSTOMIZATION SUBSYSTEM");
        extTitle.setFont(Theme.SANS_XS);
        extTitle.setForeground(Theme.ACC);
        actions.add(extTitle);
        actions.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton formsBtn = new JButton("Fetch External Forms");
        formsBtn.setBackground(Theme.ACC);
        formsBtn.setForeground(Color.BLACK);
        formsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formsBtn.addActionListener(e -> {
            log("Requesting External Forms...");
            List<FormSummary> forms = service.getExternalForms();
            log("Retrieved " + forms.size() + " forms from Customization.");
            StringBuilder sb = new StringBuilder("=== EXTERNAL FORMS RETRIEVED ===\n\n");
            for (FormSummary f : forms) {
                sb.append("FORM: ").append(f.formName).append("\n");
                sb.append("  ID: ").append(f.formId).append("\n");
                sb.append("  LAYOUT: ").append(f.layoutType).append("\n");
                
                List<FieldSummary> fields = service.getExternalFieldsForForm(f.formId);
                if (!fields.isEmpty()) {
                    sb.append("  FIELDS:\n");
                    for (FieldSummary field : fields) {
                        sb.append("    * ").append(field.fieldName)
                          .append(" (").append(field.fieldType).append(")")
                          .append(field.isMandatory ? " [REQUIRED]" : "").append("\n");
                    }
                }
                sb.append("-----------------------------------\n");
            }
            showIntegrationDataDialog("Integration: Forms & Fields", sb.toString());
        });
        actions.add(formsBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton workflowsBtn = new JButton("Fetch External Workflows");
        workflowsBtn.setBackground(Theme.ACC);
        workflowsBtn.setForeground(Color.BLACK);
        workflowsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        workflowsBtn.addActionListener(e -> {
            log("Requesting External Workflows...");
            List<WorkflowSummary> workflows = service.getExternalWorkflows();
            log("Retrieved " + workflows.size() + " workflows from Customization.");
            StringBuilder sb = new StringBuilder("=== EXTERNAL WORKFLOWS RETRIEVED ===\n\n");
            for (WorkflowSummary w : workflows) {
                sb.append("WORKFLOW: ").append(w.workflowName).append("\n");
                sb.append("  ID: ").append(w.workflowId).append("\n");
                sb.append("  STATUS: ").append(w.currentStatus).append("\n");
                sb.append("  ASSIGNEE: ").append(w.assignedTo).append("\n");
                
                List<WorkflowStepSummary> steps = service.getExternalWorkflowSteps(w.workflowId);
                if (!steps.isEmpty()) {
                    sb.append("  APPROVAL CHAIN:\n");
                    for (WorkflowStepSummary step : steps) {
                        sb.append("    [").append(step.stepId).append("] ")
                          .append(step.stepName).append(" -> ").append(step.assignee)
                          .append(" (").append(step.escalationHours).append("h SLA)\n");
                    }
                }
                sb.append("-----------------------------------\n");
            }
            showIntegrationDataDialog("Integration: Workflows & Steps", sb.toString());
        });
        actions.add(workflowsBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton taskFlowsBtn = new JButton("Fetch External Task Flows");
        taskFlowsBtn.setBackground(Theme.ACC);
        taskFlowsBtn.setForeground(Color.BLACK);
        taskFlowsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskFlowsBtn.addActionListener(e -> {
            log("Requesting External Task Flows...");
            List<TaskFlowSummary> taskFlows = service.getExternalTaskFlows();
            log("Retrieved " + taskFlows.size() + " task flows from Customization.");
            StringBuilder sb = new StringBuilder("=== EXTERNAL TASK FLOWS RETRIEVED ===\n\n");
            for (TaskFlowSummary t : taskFlows) {
                sb.append("TASK FLOW: ").append(t.flowName).append("\n");
                sb.append("  ID: ").append(t.taskFlowId).append("\n");
                sb.append("  LINKED MENU: ").append(t.linkedMenu).append("\n");
                
                List<String> windows = service.getExternalTaskFlowWindows(t.taskFlowId);
                if (!windows.isEmpty()) {
                    sb.append("  SCREEN SEQUENCE:\n");
                    for (int i = 0; i < windows.size(); i++) {
                        sb.append("    ").append(i + 1).append(". ").append(windows.get(i)).append("\n");
                    }
                }
                sb.append("-----------------------------------\n");
            }
            showIntegrationDataDialog("Integration: Task Flows & Screens", sb.toString());
        });
        actions.add(taskFlowsBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel reportTitle = new JLabel("INTEGRATED REPORT GENERATION");
        reportTitle.setFont(Theme.SANS_XS);
        reportTitle.setForeground(Theme.ACC);
        actions.add(reportTitle);
        actions.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton reportBtn = new JButton("Generate Report via External Subsystem");
        reportBtn.setBackground(new Color(46, 204, 113)); // Green
        reportBtn.setForeground(Color.BLACK);
        reportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportBtn.addActionListener(e -> {
            log("Requesting External Report Generation...");
            String report = service.generateExternalReport("Full Subsystem Audit");
            log("Report generated successfully using shared DTOs.");
            showIntegrationDataDialog("External System Generated Report", report);
        });
        actions.add(reportBtn);

        left.add(new JScrollPane(actions), BorderLayout.CENTER);
        main.add(left);

        // Right Side: Live Bus Log and Integration Status
        JPanel right = new JPanel(new BorderLayout(0, 16));
        right.setBackground(Theme.BG);
        
        // Integration Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Theme.SUR);
        statusPanel.setBorder(BorderFactory.createTitledBorder("INTEGRATION STATUS"));
        
        statusArea = new JTextArea();
        statusArea.setBackground(Theme.SUR2);
        statusArea.setForeground(Color.GREEN);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusArea.setEditable(false);
        statusArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateIntegrationStatus();
        
        statusPanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        right.add(statusPanel, BorderLayout.NORTH);

        JLabel logTitle = new JLabel("CROSS-SUBSYSTEM COMMUNICATION LOG");
        logTitle.setFont(Theme.SANS_XS);
        logTitle.setForeground(Theme.MU);
        right.add(logTitle, BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setBackground(Theme.SUR2);
        logArea.setForeground(new Color(170, 170, 170));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        right.add(new JScrollPane(logArea), BorderLayout.SOUTH);

        main.add(right);
        add(main, BorderLayout.CENTER);

        // Auto refresh
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            refreshLog();
            updateIntegrationStatus();
        });
        timer.start();
    }

    private void updateIntegrationStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECTED SUBSYSTEMS:\n");
        sb.append("----------------------\n");
        sb.append("- PerformanceManagement (Local)\n");
        sb.append("- CustomizationSubsystem (External)\n");
        sb.append("- HR_Database_Subsystem (External)\n\n");
        
        sb.append("RETRIEVED DATA SUMMARY:\n");
        sb.append("----------------------\n");
        try {
            List<FormSummary> forms = service.getExternalForms();
            sb.append("- External Forms: ").append(forms.size()).append(" detected\n");
            
            List<WorkflowSummary> workflows = service.getExternalWorkflows();
            sb.append("- External Workflows: ").append(workflows.size()).append(" detected\n");
            
            List<TaskFlowSummary> taskFlows = service.getExternalTaskFlows();
            sb.append("- External Task Flows: ").append(taskFlows.size()).append(" detected\n");
        } catch (Exception e) {
            sb.append("- Error fetching integration data.\n");
        }
        
        statusArea.setText(sb.toString());
    }

    private void log(String msg) {
        bus.publish("pm:integration:info", msg);
    }

    private void showIntegrationDataDialog(String title, String content) {
        JDialog dialog = new JDialog((Frame)null, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JTextArea area = new JTextArea(content);
        area.setBackground(Theme.SUR);
        area.setForeground(Theme.TX);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel p = new JPanel();
        p.setBackground(Theme.SUR);
        p.add(close);
        dialog.add(p, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void showRegisterDialog() {
        JTextField name = new JTextField("PayrollSystem");
        if (JOptionPane.showConfirmDialog(this, name, "Subsystem Name to Register", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String sysName = name.getText();
            if (!sysName.isEmpty()) {
                Map<String, Function<Object[], Object>> methods = new HashMap<>();
                methods.put("ping", args -> "Integrated: Data sync with " + sysName + " database active.");
                bus.register(sysName, methods);
                JOptionPane.showMessageDialog(this, "Subsystem '" + sysName + "' integrated successfully!");
            }
        }
    }

    private void refreshLog() {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> log = bus.getLog();
        for (Map<String, Object> entry : log) {
            String type = entry.get("type").toString().toUpperCase();
            sb.append(String.format("[%s] %s: %s\n", 
                type,
                entry.get("source"),
                entry.get("msg")
            ));
        }
        logArea.setText(sb.toString());
    }
}
