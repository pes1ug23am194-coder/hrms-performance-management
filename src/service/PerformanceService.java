package service;

import interfaces.*;
import impl.*;
import models.*;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class PerformanceService implements IPerformanceSubsystem, IPerformanceForCustomization {
    private static PerformanceService instance;
    private IEmployeeRepository empRepo;
    private IGoalRepository goalRepo;
    private IKPIRepository kpiRepo;
    private IAppraisalRepository appraisalRepo;
    private IFeedbackRepository feedbackRepo;

    private ICustomizationForPM customizationClient;

    private PerformanceService() {
        this.empRepo = new EmployeeRepository();
        this.goalRepo = new GoalRepository();
        this.kpiRepo = new KPIRepository();
        this.appraisalRepo = new AppraisalRepository();
        this.feedbackRepo = new FeedbackRepository();
        this.customizationClient = new CustomizationClient(); // Initialize integration client

        registerSubsystem();
    }

    public static synchronized PerformanceService getInstance() {
        if (instance == null) {
            instance = new PerformanceService();
        }
        return instance;
    }

    private void registerSubsystem() {
        Map<String, Function<Object[], Object>> methods = new HashMap<>();
        methods.put("getEmployees", args -> getAllEmployees());
        methods.put("getEmployee", args -> getEmployeeById((String)args[0]));
        methods.put("getGoals", args -> (args != null && args.length > 0 && args[0] != null && !args[0].toString().isEmpty()) ? getGoalsByEmployee((String)args[0]) : getAllGoals());
        methods.put("getFeedback", args -> (args != null && args.length > 0 && args[0] != null && !args[0].toString().isEmpty()) ? getFeedbackForEmployee((String)args[0]) : getAllFeedback());
        methods.put("getAppraisals", args -> (args != null && args.length > 0 && args[0] != null && !args[0].toString().isEmpty()) ? getAppraisalsByEmployee((String)args[0]) : getAllAppraisals());
        methods.put("getKPIs", args -> getAllKPIs());
        methods.put("getAppraisalCycles", args -> getAllPerformanceCycles());
        methods.put("getGoalsForCycle", args -> getGoalsForCycle((String) args[0]));
        methods.put("getAppraisalsForCycle", args -> getAppraisalsForCycle((String) args[0]));
        methods.put("getServerPort", args -> getServerPort());
        methods.put("getFormIds", args -> getFormIds());
        methods.put("getWorkflowIds", args -> getWorkflowIds());
        methods.put("getTaskFlowIds", args -> getTaskFlowIds());
        methods.put("getFormDescription", args -> getFormDescription((String) args[0]));
        methods.put("getWorkflowDescription", args -> getWorkflowDescription((String) args[0]));
        methods.put("getTaskFlowDescription", args -> getTaskFlowDescription((String) args[0]));
        
        IntegrationBus.getInstance().register("PerformanceManagement", methods);
    }

    // --- Employee Methods ---
    public List<Employee> getAllEmployees() { return empRepo.getAllEmployees(); }
    public Employee getEmployeeById(String id) { return empRepo.getEmployeeById(id); }
    public void addEmployee(Employee emp) { 
        empRepo.addEmployee(emp); 
        logAction("CREATE", "Employee", emp.getEmployeeId(), "Added employee: " + emp.getName());
        IntegrationBus.getInstance().publish("pm:employee:added", emp);
    }
    public void updateEmployee(Employee emp) { 
        empRepo.updateEmployee(emp); 
        logAction("UPDATE", "Employee", emp.getEmployeeId(), "Updated employee: " + emp.getName());
        IntegrationBus.getInstance().publish("pm:employee:updated", emp);
    }
    public void deleteEmployee(String id) { 
        empRepo.deleteEmployee(id); 
        logAction("DELETE", "Employee", id, "Deleted employee ID: " + id);
        IntegrationBus.getInstance().publish("pm:employee:deleted", id);
    }

    // --- Goal Methods ---
    public List<Goal> getAllGoals() { return goalRepo.getAllGoals(); }
    public List<Goal> getGoalsByEmployee(String id) { return goalRepo.getGoalsByEmployee(id); }
    public void addGoal(Goal g) { 
        goalRepo.addGoal(g); 
        IntegrationBus.getInstance().publish("pm:goal:added", g);
    }
    public void updateGoal(Goal g) { 
        goalRepo.updateGoal(g); 
        IntegrationBus.getInstance().publish("pm:goal:updated", g);
    }
    public void deleteGoal(String id) { 
        goalRepo.deleteGoal(id); 
        IntegrationBus.getInstance().publish("pm:goal:deleted", id);
    }

    // --- KPI Methods ---
    public List<KPI> getAllKPIs() { return kpiRepo.getAllKPIs(); }
    public void updateKPI(KPI k) { kpiRepo.updateKPI(k); }

    // --- Feedback Methods ---
    public List<Feedback> getAllFeedback() { return feedbackRepo.getAllFeedback(); }
    public List<Feedback> getFeedbackForEmployee(String id) { return feedbackRepo.getFeedbackForEmployee(id); }
    public void addFeedback(Feedback f) { 
        feedbackRepo.addFeedback(f); 
        IntegrationBus.getInstance().publish("pm:feedback:added", f);
    }
    public void deleteFeedback(String id) { feedbackRepo.deleteFeedback(id); }

    // --- Appraisal Methods ---
    public List<Appraisal> getAllAppraisals() { return appraisalRepo.getAllAppraisals(); }
    public List<Appraisal> getAppraisalsByEmployee(String id) { return appraisalRepo.getAppraisalsByEmployee(id); }
    public void addAppraisal(Appraisal a) { 
        appraisalRepo.addAppraisal(a); 
        IntegrationBus.getInstance().publish("pm:appraisal:added", a);
    }

    // --- Customization / Integration Methods ---
    public String getServerPort() {
        return "8080";
    }

    @Override
    public List<String> getFormIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT CAST(id AS TEXT) as idStr FROM ext_custom_forms";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ids.add(rs.getString("idStr"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    @Override
    public List<String> getWorkflowIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT CAST(id AS TEXT) as idStr FROM ext_workflows";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ids.add(rs.getString("idStr"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    @Override
    public List<String> getTaskFlowIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT CAST(id AS TEXT) as idStr FROM ext_taskflows";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ids.add(rs.getString("idStr"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }

    public List<PerformanceCycle> getAllPerformanceCycles() {
        List<PerformanceCycle> cycles = new ArrayList<>();
        String sql = "SELECT * FROM performance_cycles";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cycles.add(new PerformanceCycle(
                    rs.getInt("cycleId"),
                    rs.getString("name"),
                    rs.getString("type"),
                    parseDate(rs.getString("startDate")),
                    parseDate(rs.getString("endDate"))
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        if (cycles.isEmpty()) {
            return Arrays.asList(
                new PerformanceCycle(1, "2025 Annual Review", "ANNUAL", createDate(2025, 1, 1), createDate(2025, 12, 31)),
                new PerformanceCycle(2, "2025 Q2 Checkpoint", "QUARTERLY", createDate(2025, 4, 1), createDate(2025, 6, 30))
            );
        }
        return cycles;
    }

    private java.util.Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return new java.util.Date();
        }
    }

    public List<Goal> getGoalsForCycle(String cycleName) {
        return getAllGoals();
    }

    public List<Appraisal> getAppraisalsForCycle(String cycleName) {
        if (cycleName == null || cycleName.isBlank()) {
            return getAllAppraisals();
        }
        List<Appraisal> filtered = new ArrayList<>();
        for (Appraisal a : getAllAppraisals()) {
            if (cycleName.equalsIgnoreCase(a.getCycle())) {
                filtered.add(a);
            }
        }
        return filtered;
    }

    public String getFormDescription(String formId) {
        return switch (formId) {
            case "101" -> "External Goal Entry Form from Customization Subsystem";
            case "102" -> "External Appraisal Review Form from Customization Subsystem";
            case "103" -> "External Feedback Form from Customization Subsystem";
            default -> "Unknown form ID";
        };
    }

    public String getWorkflowDescription(String workflowId) {
        return switch (workflowId) {
            case "201" -> "External Goal Approval Workflow from Customization Subsystem";
            case "202" -> "External Appraisal Cycle Workflow from Customization Subsystem";
            default -> "Unknown workflow ID";
        };
    }

    public String getTaskFlowDescription(String taskFlowId) {
        return switch (taskFlowId) {
            case "301" -> "External Goal Planning Task Flow";
            case "302" -> "External Appraisal Process Task Flow";
            default -> "Unknown task flow ID";
        };
    }

    private java.util.Date createDate(int year, int month, int day) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // --- Integration with Customization Subsystem ---
    public List<FormSummary> getExternalForms() {
        return customizationClient.getAllForms();
    }

    public List<WorkflowSummary> getExternalWorkflows() {
        return customizationClient.getAllWorkflows();
    }

    public List<TaskFlowSummary> getExternalTaskFlows() {
        return customizationClient.getAllTaskFlows();
    }

    public List<FieldSummary> getExternalFieldsForForm(int formId) {
        return customizationClient.getFieldsForForm(formId);
    }

    public List<WorkflowStepSummary> getExternalWorkflowSteps(int workflowId) {
        return customizationClient.getWorkflowSteps(workflowId);
    }

    public List<String> getExternalTaskFlowWindows(int taskFlowId) {
        return customizationClient.getTaskFlowWindows(taskFlowId);
    }

    public List<String> getExternalLookupValues(String lookupType) {
        return customizationClient.getLookupValues(lookupType);
    }

    // --- Reward Methods ---
    public List<Reward> getAllRewards() {
        List<Reward> rewards = new ArrayList<>();
        String sql = "SELECT * FROM rewards";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rewards.add(new Reward(
                    rs.getString("rewardId"),
                    rs.getString("employeeId"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("reason"),
                    rs.getString("date")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rewards;
    }

    public void addReward(Reward r) {
        String sql = "INSERT INTO rewards (rewardId, employeeId, type, amount, reason, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getRewardId());
            pstmt.setString(2, r.getEmpId());
            pstmt.setString(3, r.getType());
            pstmt.setDouble(4, r.getAmount());
            pstmt.setString(5, r.getReason());
            pstmt.setString(6, r.getDate());
            pstmt.executeUpdate();
            logAction("CREATE", "Reward", r.getRewardId(), "Added reward for employee: " + r.getEmpId());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteReward(String id) {
        String sql = "DELETE FROM rewards WHERE rewardId = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            logAction("DELETE", "Reward", id, "Deleted reward ID: " + id);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Training Suggestion Methods ---
    public List<TrainingSuggestion> getAllTrainingSuggestions() {
        List<TrainingSuggestion> list = new ArrayList<>();
        String sql = "SELECT * FROM training_suggestions";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TrainingSuggestion(
                    rs.getString("suggestionId"),
                    rs.getString("employeeId"),
                    rs.getString("skillName"),
                    rs.getString("courseName"),
                    rs.getString("priority"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void addTrainingSuggestion(TrainingSuggestion s) {
        String sql = "INSERT INTO training_suggestions (suggestionId, employeeId, skillName, courseName, priority, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getId());
            pstmt.setString(2, s.getEmpId());
            pstmt.setString(3, s.getSkillName());
            pstmt.setString(4, s.getCourseName());
            pstmt.setString(5, s.getPriority());
            pstmt.setString(6, s.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Dashboard Stats ---
    public int getAverageScore() {
        String sql = "SELECT AVG(score) FROM employees";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getGoalsOnTrackCount() {
        List<Goal> goals = getAllGoals();
        int count = 0;
        for (Goal g : goals) {
            if ("on-track".equals(g.getStatus()) || "completed".equals(g.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getGoalsOnTrackPercentage() {
        List<Goal> goals = getAllGoals();
        if (goals.isEmpty()) return 0;
        return (getGoalsOnTrackCount() * 100) / goals.size();
    }

    // --- Audit Logging ---
    private void logAction(String action, String type, String id, String details) {
        AuditLog log = new AuditLog(0, "SYSTEM", action, type, id, details, "");
        new AuditLogRepository().logAction(log);
    }

    // --- Skills & Competencies Methods ---
    public List<String> getAllSkills() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM skills";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> getAllCompetencies() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM competencies";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Map<String, Integer> getEmployeeSkills(String empId) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT s.name, es.currentLevel FROM employee_skills es JOIN skills s ON es.skillId = s.skillId WHERE es.employeeId = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, empId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("currentLevel"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // --- Integration Report Feature ---
    public String generateExternalReport(String reportType) {
        // Demonstrate using external subsystem to generate a report
        // This call goes PM -> CustomizationClient -> IntegrationBus -> PerformanceCustomizationSubsystem
        // Then PerformanceCustomizationSubsystem -> IntegrationBus -> PerformanceService (back here)
        return customizationClient.generateExternalReport(reportType);
    }
}
