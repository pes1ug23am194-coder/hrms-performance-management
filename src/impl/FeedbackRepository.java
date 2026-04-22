package impl;

import interfaces.IFeedbackRepository;
import models.Feedback;
import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class FeedbackRepository implements IFeedbackRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Feedback> getFeedbackForEmployee(String employeeId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE employeeId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (feedbackId, employeeId, reviewerFrom, type, category, text, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, feedback.getId());
            pstmt.setString(2, feedback.getEmpId());
            pstmt.setString(3, feedback.getFrom());
            pstmt.setString(4, feedback.getType());
            pstmt.setString(5, feedback.getCategory());
            pstmt.setString(6, feedback.getText());
            pstmt.setString(7, feedback.getDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFeedback(String id) {
        String sql = "DELETE FROM feedback WHERE feedbackId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        return new Feedback(
            rs.getString("feedbackId"),
            rs.getString("employeeId"),
            rs.getString("reviewerFrom"),
            rs.getString("type"),
            rs.getString("category"),
            rs.getString("text"),
            rs.getString("date")
        );
    }
}
