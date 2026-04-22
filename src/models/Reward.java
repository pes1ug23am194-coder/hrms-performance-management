package models;

public class Reward {
    private String rewardId;
    private String empId;
    private String type; // Bonus, Promotion, Certificate, etc.
    private double amount;
    private String reason;
    private String date;

    public Reward() {}

    public Reward(String rewardId, String empId, String type, double amount, String reason, String date) {
        this.rewardId = rewardId;
        this.empId = empId;
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.date = date;
    }

    public String getRewardId() { return rewardId; }
    public void setRewardId(String rewardId) { this.rewardId = rewardId; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
