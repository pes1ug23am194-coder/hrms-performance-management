package models;

import java.util.Date;

public class AuditLog {
    private int logId;
    private String userId;
    private String action;        // CREATE, UPDATE, DELETE, VIEW
    private String entityType;    // Goal, KPI, Appraisal, etc.
    private String entityId;
    private String details;
    private Date timestamp;
    private String cycleId;

    public AuditLog() {}

    public AuditLog(int logId, String userId, String action, String entityType, String entityId, String details, String cycleId) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.cycleId = cycleId;
        this.timestamp = new Date();
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getCycleId() { return cycleId; }
    public void setCycleId(String cycleId) { this.cycleId = cycleId; }

    @Override
    public String toString() {
        return "AuditLog{id=" + logId + ", user=" + userId + ", action='" + action + "', entity=" + entityType + "#" + entityId + "}";
    }
}
