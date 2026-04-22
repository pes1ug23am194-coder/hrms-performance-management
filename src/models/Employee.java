package models;

public class Employee {
    private String employeeId;
    private String name;
    private String email;
    private String role;
    private String dept;
    private int score;
    private String trend;
    private String avatar;
    private String joinDate;
    private String manager;

    public Employee() {}

    public Employee(String employeeId, String name, String email, String role, String dept, int score, String trend, String avatar, String joinDate, String manager) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.dept = dept;
        this.score = score;
        this.trend = trend;
        this.avatar = avatar;
        this.joinDate = joinDate;
        this.manager = manager;
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    @Override
    public String toString() {
        return "Employee{id='" + employeeId + "', name='" + name + "', dept='" + dept + "', role='" + role + "'}";
    }
}
