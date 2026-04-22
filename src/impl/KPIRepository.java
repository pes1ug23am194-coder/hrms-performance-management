package impl;

import interfaces.IKPIRepository;
import models.KPI;
import service.DatabaseManager;
import java.sql.*;
import java.util.*;

public class KPIRepository implements IKPIRepository {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    public List<KPI> getAllKPIs() {
        List<KPI> list = new ArrayList<>();
        String sql = "SELECT * FROM kpis";
        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToKPI(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateKPI(KPI kpi) {
        String sql = "UPDATE kpis SET name=?, target=?, current=?, unit=?, dept=? WHERE kpiId=?";
        try (Connection conn = db.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kpi.getName());
            pstmt.setDouble(2, kpi.getTarget());
            pstmt.setDouble(3, kpi.getCurrent());
            pstmt.setString(4, kpi.getUnit());
            pstmt.setString(5, kpi.getDept());
            pstmt.setString(6, kpi.getKpiId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private KPI mapResultSetToKPI(ResultSet rs) throws SQLException {
        return new KPI(
            rs.getString("kpiId"),
            rs.getString("name"),
            rs.getDouble("target"),
            rs.getDouble("current"),
            rs.getString("unit"),
            rs.getString("dept")
        );
    }
}
