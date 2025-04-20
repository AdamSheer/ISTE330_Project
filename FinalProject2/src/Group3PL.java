import javax.swing.*;
import java.sql.*;
import java.security.MessageDigest;

public class Group3PL {
    public static void main(String[] args) {
        new Group3PL().start();
    }


    private void start() {
        boolean running = true;

        while (running) {
            String[] options = {"Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Welcome to LearnX!",
                    "LearnX Portal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                login();
            } else {
                running = false;
            }
        }
    }

    private void login() {
        String userId = JOptionPane.showInputDialog("Enter your User ID:");
        String password = JOptionPane.showInputDialog("Enter your Password:");

        String hashed = sha1(password);

        boolean isValid = Group3BL.loginUser(userId, hashed);

        if (isValid) {
            String role = Group3BL.getUserRole(userId);
            JOptionPane.showMessageDialog(null, "Login successful! Role: " + role);

            if ("admin".equals(role)) {
                adminMenu(userId);
            } else {
                clientMenu(userId);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Login failed. Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminMenu(String userId) {
        String[] options = {"Add Client", "Add Admin", "View Table", "Logout"};
        boolean loggedIn = true;

        while (loggedIn) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Admin Menu",
                    "Welcome Admin",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0 -> Group3BL.addNewClient();
                case 1 -> Group3BL.addNewAdmin();
                case 2 -> {
                    String tableName = JOptionPane.showInputDialog("Enter table name to view:");
                    Group3BL.viewTable(tableName);
                }
                case 3 -> loggedIn = false;
            }
        }
    }

    private void clientMenu(String userId) {
        String[] options = {"View My Info", "Transfer Credits", "Logout"};
        boolean loggedIn = true;

        while (loggedIn) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Client Menu",
                    "Welcome Student",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0 -> Group3BL.viewStudentInfo(userId);
                case 1 -> {
                    String toUser = JOptionPane.showInputDialog("Enter recipient User ID:");
                    String amountStr = JOptionPane.showInputDialog("Enter amount to transfer:");
                    double amount = Double.parseDouble(amountStr);

                    Group3BL.transferCredits(userId, toUser, amount);
                }
                case 2 -> loggedIn = false;
            }
        }
    }

    // Hash password using SHA-1
    private String sha1(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
