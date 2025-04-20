import java.security.MessageDigest;
import javax.swing.*;

public class Group3PL {
    public static void main(String[] args) {
        new Group3PL().start();
    }

    /*
     * Main method
     * 
     * This method initializes the application and starts the user interface.
     * It creates an instance of the Group3PL class and calls the start method.
     * The start method displays the welcome message and prompts the user to log in
     * or exit the application.
     */
    private void start() {
        // boolean variable to control the running state of the application
        boolean running = true;

        // Display a welcome message using JOptionPane
        while (running) {
            // Display a dialog with options to login or exit
            String[] options = {"Login", "Exit"};
            // Show an option dialog with the welcome message
            int choice = JOptionPane.showOptionDialog(null,
                    "Welcome to LearnX!","LearnX Portal",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Handle user choices
            if (choice == 0) { // If the user chooses to login
                // Call the login method to prompt for credentials
                login();
            } else { // If the user chooses to exit
                // Display a goodbye message
                running = false;
            }
        }
    }

    /*
     * Login method
     * 
     * This method handles user login by prompting for user ID and password.
     * It validates the credentials and determines the user's role (admin or client).
     * Depending on the role, it directs the user to the appropriate menu.
     */
    private void login() {
        // Prompt for user ID and password
        String userId = JOptionPane.showInputDialog("Enter your User ID:");
        String password = JOptionPane.showInputDialog("Enter your Password:");

        // Validate user ID and password
        String hashed = sha1(password);

        // Check if the user ID and hashed password are valid
        boolean isValid = Group3BL.loginUser(userId, hashed);

        // If valid, get the user role and display a success message
        if (isValid) {
            // Fetch the user role (admin or client)
            String role = Group3BL.getUserRole(userId);
            // Display a success message with the user's role
            JOptionPane.showMessageDialog(null, "Login successful! Role: " + role);

            // Depending on the role, display the appropriate menu
            if ("admin".equals(role)) {
                // Display the admin menu
                adminMenu(userId);
            } else {
                // Display the client menu
                clientMenu(userId);
            }
        } else { // If invalid, display an error message
            JOptionPane.showMessageDialog(null, "Login failed. Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Admin Menu
     * 
     * @param userId The ID of the logged-in user
     * 
     * This method displays the admin menu options and handles user interactions.
     * It allows the admin to add clients, add admins, view tables, or log out.
     */
    private void adminMenu(String userId) {
        // Admin menu options
        String[] options = {"Add Client", "Add Admin", "View Table", "Logout"};
        boolean loggedIn = true;

        // Loop until the user logs out
        while (loggedIn) {
            // Display the admin menu using JOptionPane
            int choice = JOptionPane.showOptionDialog(null,
                    "Admin Menu",
                    "Welcome Admin",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Handle admin choices
            if (choice == 0) { // If the admin chooses to add a client
                // Prompt for client details
                Group3BL.addNewClient();
            } else if (choice == 1) { // If the admin chooses to add an admin
                // Prompt for admin details
                Group3BL.addNewAdmin();
            } else if (choice == 2) { // If the admin chooses to view a table
                // Prompt for table selection
                String[] tables = {"Users", "Accounts", "Courses", "Enrollments", "Transactions"};
                // Display a dialog to select a table
                String selectedTable = (String) JOptionPane.showInputDialog(
                        null,
                        "Select a table to view:",
                        "View Table",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tables,
                        tables[0]
                );
                
                // If a table is selected, view the table
                if (selectedTable != null) {
                    // Call the viewTable method from Group3BL to display the selected table
                    Group3BL.viewTable(selectedTable);
                }
            } else if (choice == 3) { // If the admin chooses to log out
                // Log out and exit the admin menu
                loggedIn = false;
            }
        }
    }

    /*
     * Client Menu
     * 
     * @param userId The ID of the logged-in user
     * 
     * This method displays the client menu options and handles user interactions.
     * It allows the client to view their information, transfer money, or log out.
     */
    private void clientMenu(String userId) {
        // Client menu options
        String[] options = {"View My Info", "Transfer Money", "Logout"};
        boolean loggedIn = true;

        // Loop until the user logs out
        while (loggedIn) {
            // Display the client menu using JOptionPane
            int choice = JOptionPane.showOptionDialog(null,
                    "Client Menu",
                    "Welcome Student",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            // Handle user choices
            if (choice == 0) { // If the user chooses to view their info
                Group3BL.viewStudentInfo(userId); // Display student info
            } else if (choice == 1) { // If the user chooses to transfer money
                // Prompt for recipient user ID and amount to transfer
                String toUser = JOptionPane.showInputDialog("Enter recipient User ID:");
                String amountStr = JOptionPane.showInputDialog("Enter amount to transfer:");
                
                // Validate amount input
                double amount = Double.parseDouble(amountStr);

                // Perform the money transfer
                Group3BL.transferMoney(userId, toUser, amount);
            } 
            else if (choice == 2) { // If the user chooses to log out   
                loggedIn = false;
            }
        }
    }

    /*
     * Hash password using SHA-1
     * 
     * @param input The password to hash
     * 
     * @return The hashed password as a hexadecimal string
     * 
     * @throws NoSuchAlgorithmException If the SHA-1 algorithm is not available
     * @throws UnsupportedEncodingException If the UTF-8 encoding is not supported\
     * @throws Exception If any other error occurs during hashing
     * 
     * This method uses the SHA-1 algorithm to hash the input password and returns
     * the hashed password as a hexadecimal string. It handles exceptions that may
     * occur during the hashing process, such as NoSuchAlgorithmException and
     * UnsupportedEncodingException. The method uses a StringBuilder to build the
     * hashed password string efficiently. The resulting hashed password is
     * suitable for secure storage and comparison during user authentication.
     */
    private String sha1(String input) {
        //try catch
        try {
            // Create a MessageDigest instance for SHA-1
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            
            // Hash the input password
            byte[] result = mDigest.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            
            // Loop through each byte in the result array
            for (byte b : result) {
                sb.append(String.format("%02x", b)); // Convert each byte to a two-digit hexadecimal representation
            }
            return sb.toString(); // Return the hashed password as a hexadecimal string
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
