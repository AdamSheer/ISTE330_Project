import java.sql.*;
import java.util.*;

public class Group3DL {

    // Database connection details
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/iste330";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    /*
     * Hashing method
     * 
     * @param password the password to be hashed
     * @return the hashed password as a string
     * 
     * This method hashes the input password using SHA-1 algorithm.
     * It returns the hashed password as a hexadecimal string.
     */
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

    /*
     * Login method
     * 
     * @param userId the user ID entered by the user
     * 
     * @return true if login is successful, false otherwise
     * 
     * @throws NoSuchAlgorithmException if the hashing algorithm is not found
     * 
     * This method handles user login by prompting for user ID and password.
     * It validates the credentials and determines the user's role (admin or client).
     * Depending on the role, it directs the user to the appropriate menu.
     */
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

    /*
     * Insert user method
     * 
     * @param id the user ID to be inserted
     * @param name the name of the user
     * @param hash the hashed password of the user
     * @param role the role of the user (admin or client)
     * 
     * @return true if the user is successfully inserted, false otherwise
     * 
     * @throws SQLException if there is an error during the database operation
     * This method inserts a new user into the Users table.
     * It takes the user ID, name, hashed password, and role as parameters.
     * It returns true if the insertion is successful, false otherwise.
     */
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

    /*
     * Get table as string method
     * 
     * @param tableName the name of the table to be fetched
     * 
     * @return a string representation of the table data
     * 
     * This method retrieves data from the specified table and returns it as a formatted string.
     * It checks if the table name is valid before executing the query.
     */
    public static String getTableAsString(String tableName) {
        List<String> allowedTables = Arrays.asList("users", "accounts", "courses", "enrollments", "transactions");

        if (!allowedTables.contains(tableName.toLowerCase())) {
            return "Access to table denied.";
        }

        StringBuilder result = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                result.append(meta.getColumnName(i)).append("\t");
            }
            result.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    result.append(rs.getString(i)).append("\t");
                }
                result.append("\n");
            }

        } catch (Exception e) {
            result.append("Error fetching table: ").append(e.getMessage());
        }

        return result.toString();
    }

    /*
     * Get student details method
     * 
     * @param userId the user ID of the student
     * 
     * @return a string representation of the student's details
     * 
     * This method retrieves the details of a student based on their user ID.
     * It returns a formatted string containing the student's ID, name, role, and balance.
     */
    public static String getStudentDetails(String userId) {
        StringBuilder result = new StringBuilder();
        String query = "SELECT u.user_id, u.name, u.role, a.balance " +
                       "FROM Users u JOIN Accounts a ON u.user_id = a.user_id " +
                       "WHERE u.user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                result.append("ID: ").append(rs.getString("user_id")).append("\n");
                result.append("Name: ").append(rs.getString("name")).append("\n");
                result.append("Role: ").append(rs.getString("role")).append("\n");
                result.append("Balance: $").append(rs.getDouble("balance")).append("\n");
            }

        } catch (Exception e) {
            result.append("Error retrieving user info: ").append(e.getMessage());
        }

        return result.toString();
    }

    /*
     * Perform money transfer method
     * 
     * @param fromUser the user ID of the sender
     * @param toUser the user ID of the recipient
     * @param amount the amount to be transferred
     * 
     * @return true if the transfer is successful, false otherwise
     * 
     * This method performs a money transfer between two users.
     * It uses a stored procedure to handle the transfer logic.
     */
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

    /*
     * Get formatted table method
     * 
     * @param tableName the name of the table to be fetched
     * 
     * @return a formatted string representation of the table data
     * 
     * This method retrieves data from the specified table and returns it as a formatted string.
     * It checks if the table name is valid before executing the query.
     */
    public static String getFormattedTable(String tableName) {
        StringBuilder result = new StringBuilder();
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
    
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
    
            // Header row
            for (int i = 1; i <= columnCount; i++) {
                result.append(String.format("%-25s", meta.getColumnName(i)));
            }
            result.append("\n");
    
            // Divider
            for (int i = 1; i <= columnCount; i++) {
                result.append("--------------------");
            }
            result.append("\n");
    
            // Data rows
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