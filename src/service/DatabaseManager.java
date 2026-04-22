package service;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hrms.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS employees (employeeId TEXT PRIMARY KEY, name TEXT, email TEXT, role TEXT, dept TEXT, score INTEGER, trend TEXT, avatar TEXT, joinDate TEXT, manager TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS goals (goalId TEXT PRIMARY KEY, employeeId TEXT, title TEXT, deadline TEXT, progress INTEGER, status TEXT, priority TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS kpis (kpiId TEXT PRIMARY KEY, name TEXT, target REAL, current REAL, unit TEXT, dept TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS feedback (feedbackId TEXT PRIMARY KEY, employeeId TEXT, reviewerFrom TEXT, type TEXT, category TEXT, text TEXT, date TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS appraisals (appraisalId TEXT PRIMARY KEY, employeeId TEXT, cycle TEXT, mgrScore INTEGER, selfScore INTEGER, peerScore INTEGER, finalScore INTEGER, status TEXT, date TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS skills (skillId TEXT PRIMARY KEY, name TEXT, category TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS employee_skills (employeeId TEXT, skillId TEXT, currentLevel INTEGER, targetLevel INTEGER, PRIMARY KEY(employeeId, skillId))");
            stmt.execute("CREATE TABLE IF NOT EXISTS competencies (competencyId TEXT PRIMARY KEY, name TEXT, description TEXT, category TEXT)");
            
            seedInitialData(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedInitialData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees");
            if (rs.next() && rs.getInt(1) < 5) {
                stmt.execute("INSERT OR IGNORE INTO employees VALUES " +
                    "('E001', 'Ayesha Raza', 'ayesha@hrms.co', 'Software Engineer', 'Engineering', 88, '+5%', 'A', '2022-03-15', 'David Chen')," +
                    "('E002', 'Marcus Owens', 'marcus@hrms.co', 'Product Manager', 'Product', 74, '-2%', 'M', '2021-07-20', 'Sarah Lee')," +
                    "('E003', 'Priya Nair', 'priya@hrms.co', 'UX Designer', 'Design', 92, '+8%', 'P', '2023-01-10', 'Tom Blake')," +
                    "('E004', 'James Wilson', 'james@hrms.co', 'Backend Developer', 'Engineering', 81, '+1%', 'J', '2022-11-05', 'David Chen')," +
                    "('E005', 'Elena Rodriguez', 'elena@hrms.co', 'QA Engineer', 'Engineering', 85, '+4%', 'E', '2023-05-12', 'David Chen')," +
                    "('E006', 'Liam O''Brien', 'liam@hrms.co', 'DevOps Engineer', 'Infrastructure', 89, '+3%', 'L', '2022-08-30', 'David Chen')," +
                    "('E007', 'Sophia Wang', 'sophia@hrms.co', 'Data Scientist', 'Data Science', 94, '+6%', 'S', '2023-02-14', 'Mark Zhang')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM goals");
            if (rs.next() && rs.getInt(1) < 3) {
                stmt.execute("INSERT OR IGNORE INTO goals VALUES " +
                        "('G001', 'E001', 'Deliver Microservices Refactor', '2025-06-30', 75, 'on-track', 'High')," +
                        "('G002', 'E001', 'Complete AWS Certification', '2025-09-01', 40, 'on-track', 'Medium')," +
                        "('G003', 'E002', 'Launch Q3 Product Roadmap', '2025-07-15', 60, 'on-track', 'Critical')," +
                        "('G004', 'E002', 'Improve NPS Score by 10pts', '2025-12-31', 20, 'at-risk', 'High')," +
                        "('G005', 'E003', 'Redesign Onboarding Flow', '2025-05-30', 90, 'completed', 'High')," +
                        "('G006', 'E003', 'Conduct 5 User Research Sessions', '2025-08-01', 60, 'on-track', 'Medium')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM kpis");
            if (rs.next() && rs.getInt(1) < 3) {
                stmt.execute("INSERT OR IGNORE INTO kpis VALUES " +
                        "('K001', 'Employee Productivity Index', 90.0, 84.5, '%', 'Engineering')," +
                        "('K002', 'Goal Completion Rate', 85.0, 72.0, '%', 'All')," +
                        "('K003', 'Average Performance Score', 88.0, 86.2, 'pts', 'All')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM feedback");
            if (rs.next() && rs.getInt(1) < 3) {
                stmt.execute("INSERT OR IGNORE INTO feedback VALUES " +
                        "('F001', 'E001', 'David Chen', 'Positive', 'Technical', 'Exceptional work on the microservices architecture. Really pushed the team forward.', '2025-03-10')," +
                        "('F002', 'E001', 'Marcus Owens', 'Constructive', 'Collaboration', 'Could improve cross-team communication during sprint planning.', '2025-03-15')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM appraisals");
            if (rs.next() && rs.getInt(1) < 3) {
                stmt.execute("INSERT OR IGNORE INTO appraisals VALUES " +
                        "('A001', 'E001', 'Q1 2025', 90, 88, 87, 88, 'Completed', '2025-03-31')," +
                        "('A002', 'E002', 'Q1 2025', 75, 72, 76, 74, 'Completed', '2025-03-31')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM skills");
            if (rs.next() && rs.getInt(1) < 3) {
                stmt.execute("INSERT OR IGNORE INTO skills VALUES " +
                        "('S01', 'Java Programming', 'Technical')," +
                        "('S02', 'Project Management', 'Soft Skill')," +
                        "('S03', 'Cloud Architecture', 'Technical')");
                stmt.execute("INSERT OR IGNORE INTO employee_skills VALUES " +
                        "('E001', 'S01', 4, 5)," +
                        "('E001', 'S03', 2, 4)," +
                        "('E002', 'S02', 4, 4)");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM competencies");
            if (rs.next() && rs.getInt(1) < 4) {
                stmt.execute("INSERT OR IGNORE INTO competencies VALUES " +
                    "('C01', 'Analytical Thinking', 'Ability to solve complex problems', 'Core')," +
                    "('C02', 'Leadership', 'Leading teams effectively', 'Management')," +
                    "('C03', 'Communication', 'Clear and concise exchange of info', 'Core')");
            }
        }
    }
}
