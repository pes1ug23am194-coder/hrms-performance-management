package impl;

import interfaces.IAppraisalRepository;
import models.Appraisal;
import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class AppraisalRepository implements IAppraisalRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public List<Appraisal> getAllAppraisals() {
        List<Appraisal> list = new ArrayList<>();
        String sql = "SELECT * FROM appraisals";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToAppraisal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appraisal> getAppraisalsByEmployee(String employeeId) {
        List<Appraisal> list = new ArrayList<>();
        String sql = "SELECT * FROM appraisals WHERE employeeId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAppraisal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addAppraisal(Appraisal a) {
        String sql = "INSERT INTO appraisals (appraisalId, employeeId, cycle, mgrScore, selfScore, peerScore, finalScore, status, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getId());
            pstmt.setString(2, a.getEmpId());
            pstmt.setString(3, a.getCycle());
            pstmt.setObject(4, a.getMgr(), java.sql.Types.INTEGER);
            pstmt.setObject(5, a.getSelf(), java.sql.Types.INTEGER);
            pstmt.setObject(6, a.getPeer(), java.sql.Types.INTEGER);
            pstmt.setObject(7, a.getFinalScore(), java.sql.Types.INTEGER);
            pstmt.setString(8, a.getStatus());
            pstmt.setString(9, a.getDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Appraisal mapResultSetToAppraisal(ResultSet rs) throws SQLException {
        return new Appraisal(
            rs.getString("appraisalId"),
            rs.getString("employeeId"),
            rs.getString("cycle"),
            (Integer) rs.getObject("mgrScore"),
            (Integer) rs.getObject("selfScore"),
            (Integer) rs.getObject("peerScore"),
            (Integer) rs.getObject("finalScore"),
            rs.getString("status"),
            rs.getString("date")
        );
    }
}
