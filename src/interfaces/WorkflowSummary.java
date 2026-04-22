package interfaces;

public class WorkflowSummary {
    public int    workflowId;
    public String workflowName;
    public String currentStatus;
    public String assignedTo;

    public WorkflowSummary(int workflowId, String workflowName,
                            String currentStatus, String assignedTo) {
        this.workflowId    = workflowId;
        this.workflowName  = workflowName;
        this.currentStatus = currentStatus;
        this.assignedTo    = assignedTo;
    }
}
