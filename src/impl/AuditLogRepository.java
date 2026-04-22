package impl;

import interfaces.IAuditLogRepository;
import models.AuditLog;
import service.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository implements IAuditLogRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public boolean logAction(AuditLog log) {
        String sql = "INSERT INTO audit_logs (userId, action, entityType, entityId, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log.getUserId());
            pstmt.setString(2, log.getAction());
            pstmt.setString(3, log.getEntityType());
            pstmt.setString(4, log.getEntityId());
            pstmt.setString(5, log.getDetails());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<AuditLog> getLogsByUser(String userId, java.util.Date from, java.util.Date to) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE userId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAuditLog(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<AuditLog> getLogsByEntity(String entityType, String entityId) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE entityType = ? AND entityId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entityType);
            pstmt.setString(2, entityId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAuditLog(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("logId"));
        log.setUserId(rs.getString("userId"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entityType"));
        log.setEntityId(rs.getString("entityId"));
        log.setDetails(rs.getString("details"));
        try {
            String ts = rs.getString("timestamp");
            if (ts != null) {
                log.setTimestamp(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ts));
            }
        } catch (Exception e) {
            log.setTimestamp(new java.util.Date());
        }
        return log;
    }

    @Override
    public List<AuditLog> getLogsByCycle(String cycleId) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE entityType = 'Cycle' AND entityId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cycleId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAuditLog(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
