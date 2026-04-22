package interfaces;

import java.util.List;

/**
 * ============================================================
 * INTERFACE: ICustomizationForPM
 * From:  Customization Subsystem
 * To:    Performance Management Subsystem
 * Used by PM Modules: Goal Tracking, Feedback & Appraisals
 * ============================================================
 */
public interface ICustomizationForPM {

    FormSummary getFormById(int formId);

    List<FormSummary> getAllForms();

    List<FieldSummary> getFieldsForForm(int formId);

    List<WorkflowSummary> getAllWorkflows();

    WorkflowSummary getWorkflowById(int workflowId);

    List<WorkflowStepSummary> getWorkflowSteps(int workflowId);

    boolean triggerWorkflow(int workflowId, String newStatus);

    List<TaskFlowSummary> getAllTaskFlows();

    TaskFlowSummary getTaskFlowById(int taskFlowId);

    List<String> getTaskFlowWindows(int taskFlowId);

    List<String> getLookupValues(String lookupType);

    String generateExternalReport(String cycleName);
}
