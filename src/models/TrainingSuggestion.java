package models;

public class TrainingSuggestion {
    private String id;
    private String empId;
    private String skillName;
    private String courseName;
    private String priority;
    private String status; // Suggested, Enrolled, Completed

    public TrainingSuggestion() {}

    public TrainingSuggestion(String id, String empId, String skillName, String courseName, String priority, String status) {
        this.id = id;
        this.empId = empId;
        this.skillName = skillName;
        this.courseName = courseName;
        this.priority = priority;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
