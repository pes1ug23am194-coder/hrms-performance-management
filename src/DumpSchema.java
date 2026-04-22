import java.sql.*;

public class DumpSchema {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:hrms.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTable: " + tableName);
                ResultSet columns = meta.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    System.out.println("  - " + columns.getString("COLUMN_NAME") + " (" + columns.getString("TYPE_NAME") + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
