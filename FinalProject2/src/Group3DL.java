import java.sql.*;
import java.util.*;

public class Group3DL {

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/iste330";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    // Validate login credentials
    public static boolean validateLogin(String userId, String hashedPassword) {
        String query = "SELECT * FROM Users WHERE user_id = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            return rs.next();  // login valid if found

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user role (admin or client)
    public static String fetchUserRole(String userId) {
        String query = "SELECT role FROM Users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Insert new user (admin/client)
    public static boolean insertUser(String id, String name, String hash, String role) {
        String query = "INSERT INTO Users (user_id, name, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, hash);
            stmt.setString(4, role);

            int rows = stmt.executeUpdate();
            return rows == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Show contents of any table (admin only) — with table whitelist
    public static String getTableAsString(String tableName) {
        List<String> allowedTables = Arrays.asList("users", "accounts", "courses", "enrollments", "transactions");

        if (!allowedTables.contains(tableName.toLowerCase())) {
            return "Access to table denied.";
        }

        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                sb.append(meta.getColumnName(i)).append("\t");
            }
            sb.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    sb.append(rs.getString(i)).append("\t");
                }
                sb.append("\n");
            }

        } catch (Exception e) {
            sb.append("Error fetching table: ").append(e.getMessage());
        }

        return sb.toString();
    }

    // View personal info (client only) — FIXED to show balance from accounts
    public static String getStudentDetails(String userId) {
        StringBuilder sb = new StringBuilder();
        String query = "SELECT u.user_id, u.name, u.role, a.balance " +
                       "FROM Users u JOIN Accounts a ON u.user_id = a.user_id " +
                       "WHERE u.user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sb.append("ID: ").append(rs.getString("user_id")).append("\n");
                sb.append("Name: ").append(rs.getString("name")).append("\n");
                sb.append("Role: ").append(rs.getString("role")).append("\n");
                sb.append("Balance: $").append(rs.getDouble("balance")).append("\n");
            }

        } catch (Exception e) {
            sb.append("Error retrieving user info: ").append(e.getMessage());
        }

        return sb.toString();
    }

    // Credit transfer using stored procedure
    public static boolean performMoneyTransfer(String fromUser, String toUser, double amount) {
        String transferProc = "{ CALL transfer_credits(?, ?, ?) }";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             CallableStatement stmt = conn.prepareCall(transferProc)) {

            stmt.setString(1, fromUser);
            stmt.setString(2, toUser);
            stmt.setDouble(3, amount);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Transfer failed: " + e.getMessage());
            return false;
        }
    }
}