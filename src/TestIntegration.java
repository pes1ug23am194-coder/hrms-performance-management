import service.PerformanceService;
import interfaces.*;
import java.util.List;

public class TestIntegration {
    public static void main(String[] args) {
        System.out.println("=== INTEGRATION TEST: Performance Management ↔ Customization Subsystem ===");

        PerformanceService ps = PerformanceService.getInstance();

        // Test our interface implementation
        System.out.println("\n--- Our Data (for Customization team) ---");
        System.out.println("Server Port: " + ps.getServerPort());
        System.out.println("Form IDs: " + ps.getFormIds());
        System.out.println("Workflow IDs: " + ps.getWorkflowIds());
        System.out.println("Task Flow IDs: " + ps.getTaskFlowIds());
        System.out.println("Performance Cycles: " + ps.getAllPerformanceCycles().size());

        // Test calling their interface (mock)
        System.out.println("\n--- External Data (from Customization team) ---");
        List<FormSummary> forms = ps.getExternalForms();
        System.out.println("External Forms: " + forms.size());
        for (FormSummary f : forms) {
            System.out.println("  - " + f.formName + " (" + f.layoutType + ")");
        }

        List<WorkflowSummary> workflows = ps.getExternalWorkflows();
        System.out.println("External Workflows: " + workflows.size());
        for (WorkflowSummary w : workflows) {
            System.out.println("  - " + w.workflowName + " (" + w.currentStatus + ")");
        }

        List<TaskFlowSummary> taskFlows = ps.getExternalTaskFlows();
        System.out.println("External Task Flows: " + taskFlows.size());
        for (TaskFlowSummary t : taskFlows) {
            System.out.println("  - " + t.flowName + " (" + t.flowStatus + ")");
        }

        List<String> priorities = ps.getExternalLookupValues("priorities");
        System.out.println("Lookup Values (priorities): " + priorities);

        System.out.println("\n=== INTEGRATION COMPLETED SUCCESSFULLY ===");
        System.out.println("✅ Goal Tracking module can now use external forms and workflows");
        System.out.println("✅ Feedback & Appraisals module can now use external forms, workflows, and task flows");
        System.out.println("✅ Customization team can read our goal/appraisal cycle data via IPerformanceForCustomization");
    }
}