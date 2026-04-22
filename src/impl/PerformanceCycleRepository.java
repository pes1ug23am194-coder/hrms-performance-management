package impl;

import interfaces.IPerformanceCycleRepository;
import models.PerformanceCycle;
import java.util.*;

import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class PerformanceCycleRepository implements IPerformanceCycleRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public List<PerformanceCycle> getAllCycles() {
        List<PerformanceCycle> list = new ArrayList<>();
        String sql = "SELECT * FROM performance_cycles";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCycle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public PerformanceCycle getCycleById(String cycleId) {
        String sql = "SELECT * FROM performance_cycles WHERE cycleId = ?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cycleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCycle(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PerformanceCycle mapResultSetToCycle(ResultSet rs) throws SQLException {
        try {
            return new PerformanceCycle(
                rs.getInt("cycleId"),
                rs.getString("name"),
                rs.getString("type"),
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("startDate")),
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("endDate"))
            );
        } catch (Exception e) {
            return new PerformanceCycle(rs.getInt("cycleId"), rs.getString("name"), rs.getString("type"), new java.util.Date(), new java.util.Date());
        }
    }
}
