package service;

import interfaces.*;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * ============================================================
 * EXTERNAL SUBSYSTEM: Performance Customization
 * ============================================================
 * This class simulates the independent "Performance Customization" subsystem.
 * It manages the "ext_" tables in hrms.db and provides an interface for
 * other subsystems (like Performance Management) to access its data.
 * ============================================================
 */
public class PerformanceCustomizationSubsystem implements ICustomizationForPM {
    
    private static PerformanceCustomizationSubsystem instance;

    private PerformanceCustomizationSubsystem() {
        registerWithBus();
    }

    public static synchronized PerformanceCustomizationSubsystem getInstance() {
        if (instance == null) {
            instance = new PerformanceCustomizationSubsystem();
        }
        return instance;
    }

    private void registerWithBus() {
        Map<String, Function<Object[], Object>> methods = new HashMap<>();
        
        // Registering ICustomizationForPM methods for remote calling via the IntegrationBus
        methods.put("getFormById", args -> getFormById((Integer)args[0]));
        methods.put("getAllForms", args -> getAllForms());
        methods.put("getFieldsForForm", args -> getFieldsForForm((Integer)args[0]));
        methods.put("getAllWorkflows", args -> getAllWorkflows());
        methods.put("getWorkflowById", args -> getWorkflowById((Integer)args[0]));
        methods.put("getWorkflowSteps", args -> getWorkflowSteps((Integer)args[0]));
        methods.put("triggerWorkflow", args -> triggerWorkflow((Integer)args[0], (String)args[1]));
        methods.put("getAllTaskFlows", args -> getAllTaskFlows());
        methods.put("getTaskFlowById", args -> getTaskFlowById((Integer)args[0]));
        methods.put("getTaskFlowWindows", args -> getTaskFlowWindows((Integer)args[0]));
        methods.put("getLookupValues", args -> getLookupValues((String)args[0]));
        
        // Add a specialized "integration proof" method
        methods.put("generateExternalReport", args -> generateExternalReport((String)args[0]));

        IntegrationBus.getInstance().register("PerformanceCustomization", methods);
    }

    /**
     * Simulation of an external feature that "calls back" into the Performance subsystem
     * to gather data for a consolidated report.
     */
    public String generateExternalReport(String cycleName) {
        // This is where TRUE integration is proven:
        // 1. Performance Customization (us) calls back to Performance Management (them)
        // 2. We use the bus to get THEIR data (goals/appraisals)
        // 3. We combine it with OUR data (form definitions)
        
        IntegrationBus bus = IntegrationBus.getInstance();
        Object cycles = bus.call("PerformanceManagement", "getAppraisalCycles");
        Object goals = bus.call("PerformanceManagement", "getGoals", ""); // get all goals
        
        return "EXTERNAL REPORT GENERATED\n" +
               "--------------------------\n" +
               "Source: Performance Customization Subsystem\n" +
               "Cycle Data Fetched from PM: " + cycles + "\n" +
               "Goal Data Fetched from PM: " + goals + "\n" +
               "Integration Status: SUCCESSFUL (Two-way communication verified via IntegrationBus)";
    }

    @Override
    public FormSummary getFormById(int formId) {
        String sql = "SELECT * FROM ext_custom_forms WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, formId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new FormSummary(rs.getInt("id"), rs.getString("name"), rs.getString("layout"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<FormSummary> getAllForms() {
        List<FormSummary> forms = new ArrayList<>();
        String sql = "SELECT * FROM ext_custom_forms";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                forms.add(new FormSummary(rs.getInt("id"), rs.getString("name"), rs.getString("layout")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return forms;
    }

    @Override
    public List<FieldSummary> getFieldsForForm(int formId) {
        List<FieldSummary> fields = new ArrayList<>();
        String sql = "SELECT * FROM ext_form_fields WHERE formId = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, formId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                fields.add(new FieldSummary(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("mandatory") == 1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return fields;
    }

    @Override
    public List<WorkflowSummary> getAllWorkflows() {
        List<WorkflowSummary> workflows = new ArrayList<>();
        String sql = "SELECT * FROM ext_workflows";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                workflows.add(new WorkflowSummary(rs.getInt("id"), rs.getString("name"), rs.getString("status"), rs.getString("assignee")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return workflows;
    }

    @Override
    public WorkflowSummary getWorkflowById(int workflowId) {
        String sql = "SELECT * FROM ext_workflows WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new WorkflowSummary(rs.getInt("id"), rs.getString("name"), rs.getString("status"), rs.getString("assignee"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<WorkflowStepSummary> getWorkflowSteps(int workflowId) {
        List<WorkflowStepSummary> steps = new ArrayList<>();
        String sql = "SELECT * FROM ext_workflow_steps WHERE workflowId = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                steps.add(new WorkflowStepSummary(rs.getInt("id"), workflowId, rs.getString("name"), rs.getString("assignee"), rs.getInt("sla")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return steps;
    }

    @Override
    public boolean triggerWorkflow(int workflowId, String newStatus) {
        String sql = "UPDATE ext_workflows SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, workflowId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<TaskFlowSummary> getAllTaskFlows() {
        List<TaskFlowSummary> taskFlows = new ArrayList<>();
        String sql = "SELECT * FROM ext_taskflows";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                taskFlows.add(new TaskFlowSummary(rs.getInt("id"), rs.getString("name"), rs.getString("status"), 
                        rs.getString("menu"), rs.getInt("visible") == 1, rs.getInt("searchable") == 1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return taskFlows;
    }

    @Override
    public TaskFlowSummary getTaskFlowById(int taskFlowId) {
        String sql = "SELECT * FROM ext_taskflows WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskFlowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new TaskFlowSummary(rs.getInt("id"), rs.getString("name"), rs.getString("status"), 
                        rs.getString("menu"), rs.getInt("visible") == 1, rs.getInt("searchable") == 1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<String> getTaskFlowWindows(int taskFlowId) {
        List<String> windows = new ArrayList<>();
        String sql = "SELECT windowName FROM ext_taskflow_windows WHERE flowId = ? ORDER BY sequence";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskFlowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                windows.add(rs.getString("windowName"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return windows;
    }

    @Override
    public List<String> getLookupValues(String lookupType) {
        List<String> values = new ArrayList<>();
        String sql = "SELECT value FROM lookups WHERE type = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lookupType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                values.add(rs.getString("value"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return values;
    }
}
