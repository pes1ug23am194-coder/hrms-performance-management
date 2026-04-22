package impl;

import interfaces.INotificationRepository;
import models.Notification;
import service.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository implements INotificationRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public void createNotification(Notification n) {
        String sql = "INSERT INTO notifications (notificationId, userId, title, message, timestamp, isRead) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, n.getId());
            pstmt.setString(2, n.getUserId());
            pstmt.setString(3, n.getTitle());
            pstmt.setString(4, n.getMessage());
            pstmt.setString(5, n.getTimestamp());
            pstmt.setInt(6, n.isRead() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> getNotificationsForEmployee(String employeeId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE userId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Notification(
                    rs.getString("notificationId"),
                    rs.getString("userId"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("timestamp"),
                    rs.getInt("isRead") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
