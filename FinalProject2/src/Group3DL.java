import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Group3DL {

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/iste330";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    public static boolean validateLogin(String userId, String hashedPassword) {
        String query = "SELECT * FROM Users WHERE user_id = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public static boolean insertUser(String id, String name, String password, String role) {
        String sql = "{CALL insertUser(?, ?, ?, ?)}";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             CallableStatement stmt = conn.prepareCall(sql)) {
    
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, password);
            stmt.setString(4, role);
    
            stmt.execute();
            return true;
    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
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

    public static String getFormattedTable(String tableName) {
        StringBuilder result = new StringBuilder();
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
    
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
    
            for (int i = 1; i <= columnCount; i++) {
                result.append(String.format("%-25s", meta.getColumnName(i)));
            }
            result.append("\n");
    
            for (int i = 1; i <= columnCount; i++) {
                result.append("--------------------");
            }
            result.append("\n");
    
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(String.format("%-25s", rs.getString(i)));
                }
                result.append("\n");
            }
    
        } catch (SQLException e) {
            return "Error reading table: " + e.getMessage();
        }
    
        return result.toString();
    }
    
}