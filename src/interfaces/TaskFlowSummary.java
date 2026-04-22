package interfaces;

public class TaskFlowSummary {
    public int     taskFlowId;
    public String  flowName;
    public String  flowStatus;
    public String  linkedMenu;
    public boolean validateOnNext;
    public boolean allowBackNav;

    public TaskFlowSummary(int taskFlowId, String flowName, String flowStatus,
                            String linkedMenu, boolean validateOnNext, boolean allowBackNav) {
        this.taskFlowId    = taskFlowId;
        this.flowName      = flowName;
        this.flowStatus    = flowStatus;
        this.linkedMenu    = linkedMenu;
        this.validateOnNext = validateOnNext;
        this.allowBackNav  = allowBackNav;
    }
}
