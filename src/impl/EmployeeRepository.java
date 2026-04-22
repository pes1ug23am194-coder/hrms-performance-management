package impl;

import interfaces.IEmployeeRepository;
import models.Employee;
import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class EmployeeRepository implements IEmployeeRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public Employee getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employeeId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addEmployee(Employee emp) {
        String sql = "INSERT INTO employees (employeeId, name, email, role, dept, score, trend, avatar, joinDate, manager) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getEmployeeId());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getEmail());
            pstmt.setString(4, emp.getRole());
            pstmt.setString(5, emp.getDept());
            pstmt.setInt(6, emp.getScore());
            pstmt.setString(7, emp.getTrend());
            pstmt.setString(8, emp.getAvatar());
            pstmt.setString(9, emp.getJoinDate());
            pstmt.setString(10, emp.getManager());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET name=?, email=?, role=?, dept=?, score=?, trend=?, avatar=?, joinDate=?, manager=? WHERE employeeId=?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getEmail());
            pstmt.setString(3, emp.getRole());
            pstmt.setString(4, emp.getDept());
            pstmt.setInt(5, emp.getScore());
            pstmt.setString(6, emp.getTrend());
            pstmt.setString(7, emp.getAvatar());
            pstmt.setString(8, emp.getJoinDate());
            pstmt.setString(9, emp.getManager());
            pstmt.setString(10, emp.getEmployeeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employees WHERE employeeId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getString("employeeId"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("role"),
            rs.getString("dept"),
            rs.getInt("score"),
            rs.getString("trend"),
            rs.getString("avatar"),
            rs.getString("joinDate"),
            rs.getString("manager")
        );
    }
}
