package interfaces;

import models.AuditLog;
import java.util.List;

public interface IAuditLogRepository {
    boolean logAction(AuditLog log);
    List<AuditLog> getLogsByEntity(String entityType, String entityId);
    List<AuditLog> getLogsByUser(String userId, java.util.Date from, java.util.Date to);
    List<AuditLog> getLogsByCycle(String cycleId);
}
