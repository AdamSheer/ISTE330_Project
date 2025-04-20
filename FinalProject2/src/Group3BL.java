import java.security.MessageDigest;

import javax.swing.*;

public class Group3BL {

    // Handles login validation
    public static boolean loginUser(String userId, String hashedPassword) {
        return Group3DL.validateLogin(userId, hashedPassword);
    }

    // Fetches user role (admin/client)
    public static String getUserRole(String userId) {
        return Group3DL.fetchUserRole(userId);
    }

    // Admin can add new client
    public static void addNewClient() {
        String id = JOptionPane.showInputDialog("Enter new client ID:");
        String name = JOptionPane.showInputDialog("Enter client name:");
        String password = JOptionPane.showInputDialog("Enter password:");

        String hash = Hashing.sha1(password);

        boolean success = Group3DL.insertUser(id, name, hash, "client");

        if (success) {
            JOptionPane.showMessageDialog(null, "Client added successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add client.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Admin can add new admin
    public static void addNewAdmin() {
        String id = JOptionPane.showInputDialog("Enter new admin ID:");
        String name = JOptionPane.showInputDialog("Enter admin name:");
        String password = JOptionPane.showInputDialog("Enter password:");

        String hash = Hashing.sha1(password);

        boolean success = Group3DL.insertUser(id, name, hash, "admin");

        if (success) {
            JOptionPane.showMessageDialog(null, "Admin added successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Failed to add admin.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Admin can view any table
    public static void viewTable(String tableName) {
        String tableData = Group3DL.getTableAsString(tableName);
        if (tableData != null && !tableData.isEmpty()) {
            JOptionPane.showMessageDialog(null, tableData);
        } else {
            JOptionPane.showMessageDialog(null, "Table not found or empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Client sees their own courses + info
    public static void viewStudentInfo(String userId) {
        String info = Group3DL.getStudentDetails(userId);
        if (info != null && !info.isEmpty()) {
            JOptionPane.showMessageDialog(null, info);
        } else {
            JOptionPane.showMessageDialog(null, "No info found for student.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Client transfers credits to another user
    public static void transferCredits(String fromUser, String toUser, double amount) {
        boolean success = Group3DL.performCreditTransfer(fromUser, toUser, amount);
        if (success) {
            JOptionPane.showMessageDialog(null, "Transfer successful.");
        } else {
            JOptionPane.showMessageDialog(null, "Transfer failed. Check balance or user ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class Hashing {

        // Returns the SHA-1 hash of a string formatted to 40-character hexadecimal
        public static String sha1(String input) {
            String sha1 = "";
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                byte[] dig = digest.digest(input.getBytes("utf8"));

                // Convert to 40-character hexadecimal string
                sha1 = String.format("%040x", new java.math.BigInteger(1, dig));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sha1;
        }
    }
}
