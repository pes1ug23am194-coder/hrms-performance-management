package impl;

import interfaces.IGoalRepository;
import models.Goal;
import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class GoalRepository implements IGoalRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public List<Goal> getAllGoals() {
        List<Goal> list = new ArrayList<>();
        String sql = "SELECT * FROM goals";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToGoal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Goal> getGoalsByEmployee(String employeeId) {
        List<Goal> list = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE employeeId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToGoal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addGoal(Goal goal) {
        String sql = "INSERT INTO goals (goalId, employeeId, title, deadline, progress, status, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goal.getGoalId());
            pstmt.setString(2, goal.getEmployeeId());
            pstmt.setString(3, goal.getTitle());
            pstmt.setString(4, goal.getDeadline());
            pstmt.setInt(5, goal.getProgress());
            pstmt.setString(6, goal.getStatus());
            pstmt.setString(7, goal.getPriority());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGoal(Goal goal) {
        String sql = "UPDATE goals SET employeeId=?, title=?, deadline=?, progress=?, status=?, priority=? WHERE goalId=?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goal.getEmployeeId());
            pstmt.setString(2, goal.getTitle());
            pstmt.setString(3, goal.getDeadline());
            pstmt.setInt(4, goal.getProgress());
            pstmt.setString(5, goal.getStatus());
            pstmt.setString(6, goal.getPriority());
            pstmt.setString(7, goal.getGoalId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGoal(String goalId) {
        String sql = "DELETE FROM goals WHERE goalId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        return new Goal(
            rs.getString("goalId"),
            rs.getString("employeeId"),
            rs.getString("title"),
            rs.getString("deadline"),
            rs.getInt("progress"),
            rs.getString("status"),
            rs.getString("priority")
        );
    }
}
