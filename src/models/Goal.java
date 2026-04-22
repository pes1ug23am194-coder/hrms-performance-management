package models;

public class Goal {
    private String goalId;
    private String employeeId;
    private String title;
    private String deadline;
    private int progress;
    private String status;
    private String priority;

    public Goal() {}

    public Goal(String goalId, String employeeId, String title, String deadline, int progress, String status, String priority) {
        this.goalId = goalId;
        this.employeeId = employeeId;
        this.title = title;
        this.deadline = deadline;
        this.progress = progress;
        this.status = status;
        this.priority = priority;
    }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    @Override
    public String toString() {
        return "Goal{id='" + goalId + "', empId='" + employeeId + "', title='" + title + "', progress=" + progress + "%, status='" + status + "'}";
    }
}
