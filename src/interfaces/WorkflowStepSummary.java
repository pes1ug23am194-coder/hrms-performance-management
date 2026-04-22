package interfaces;

public class WorkflowStepSummary {
    public int    stepId;
    public int    workflowId;
    public String stepName;
    public String assignee;
    public int    escalationHours;

    public WorkflowStepSummary(int stepId, int workflowId,
                                String stepName, String assignee, int escalationHours) {
        this.stepId          = stepId;
        this.workflowId      = workflowId;
        this.stepName        = stepName;
        this.assignee        = assignee;
        this.escalationHours = escalationHours;
    }
}
