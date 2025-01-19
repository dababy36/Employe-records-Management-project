package org.swingUI.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserForm extends JFrame {

    // Table and model
    private JTable table;
    private DefaultTableModel tableModel;

    // Search fields
    private JTextField searchField; // Field for searching by ID
    private JButton searchButton; // Button to trigger search

    // Buttons
    private JButton addButton, updateButton, deleteButton, refreshButton, openHRFormButton;

    // API URLs
    private static final String GET_ALL_USERS_URL = "http://localhost:8080/api/v1/user/ADMIN/users/all";
    private static final String DELETE_USER_URL = "http://localhost:8080/api/v1/user/ADMIN/users/{userId}/delete";
    private static final String GET_USER_BY_ID_URL = "http://localhost:8080/api/v1/user/ADMIN/user/{userId}";

    // JWT Token
    private String jwtToken;

    // Constructor to accept JWT token
    public UserForm(String jwtToken) {
        this.jwtToken = jwtToken;

        // Set up the frame
        setTitle("User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE instead of EXIT_ON_CLOSE
        setSize(1200, 700); // Increased size for better spacing
        setLayout(new BorderLayout());

        // Add a window listener to redirect to LoginForm when the form is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                new LoginForm().setVisible(true); // Open the login form
            }
        });

        // Create a welcome message panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(0, 102, 204)); // Blue background
        JLabel welcomeLabel = new JLabel("Welcome to Admin Space");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE); // White text
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        // Add button to open HR Form
        openHRFormButton = new JButton("Open HR Form");
        openHRFormButton.setFont(new Font("Serif", Font.BOLD, 14));
        openHRFormButton.setBackground(Color.WHITE); // White background
        openHRFormButton.setForeground(Color.BLACK); // Black text
        openHRFormButton.setFocusPainted(false); // Remove focus border
        welcomePanel.add(openHRFormButton, BorderLayout.EAST);

        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10)); // Add more padding to the welcome panel

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        searchField = new JTextField(15); // Search field for user ID
        searchButton = new JButton("Search by ID"); // Search button
        searchButton.setBackground(new Color(0, 102, 204)); // Blue background
        searchButton.setForeground(Color.BLACK); // Black text
        searchButton.setFont(new Font("Serif", Font.BOLD, 14));

        searchPanel.add(new JLabel("Search by ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create a panel to hold the welcome message and search panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical arrangement
        topPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Add components to the top panel
        topPanel.add(welcomePanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical space
        topPanel.add(searchPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Add padding

        add(topPanel, BorderLayout.NORTH);

        // Create table with updated columns
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Email", "Role"
        }, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Serif", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        addButton = new JButton("Add User");
        updateButton = new JButton("Update User");
        deleteButton = new JButton("Delete User");
        refreshButton = new JButton("Refresh Table");

        // Set button styles
        addButton.setBackground(new Color(0, 102, 204)); // Blue background
        addButton.setForeground(Color.BLACK); // Black text
        addButton.setFont(new Font("Serif", Font.BOLD, 14));

        updateButton.setBackground(new Color(0, 102, 204)); // Blue background
        updateButton.setForeground(Color.BLACK); // Black text
        updateButton.setFont(new Font("Serif", Font.BOLD, 14));

        deleteButton.setBackground(new Color(204, 0, 0)); // Red background
        deleteButton.setForeground(Color.BLACK); // Black text
        deleteButton.setFont(new Font("Serif", Font.BOLD, 14));

        refreshButton.setBackground(new Color(0, 102, 204)); // Blue background
        refreshButton.setForeground(Color.BLACK); // Black text
        refreshButton.setFont(new Font("Serif", Font.BOLD, 14));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listeners
        addButton.addActionListener(new AddActionListener());
        updateButton.addActionListener(new UpdateActionListener());
        deleteButton.addActionListener(new DeleteActionListener());
        refreshButton.addActionListener(new RefreshActionListener());
        searchButton.addActionListener(new SearchActionListener());
        openHRFormButton.addActionListener(new OpenHRFormActionListener()); // Add action listener for Open HR Form button

        // Fetch initial data automatically with JWT authentication
        refreshTable();

        // Center the frame on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Fetch all users from API and populate the table
    private void fetchAllUsers() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Fetch the response from the API
                    String response = sendAuthenticatedGetRequest(GET_ALL_USERS_URL);
                    System.out.println("API Response: " + response);

                    // Parse the response as a JSON object
                    JSONObject jsonResponse = new JSONObject(response);

                    // Check if the response contains the "data" field
                    if (jsonResponse.has("data")) {
                        // Extract the "data" field as a JSON array
                        JSONArray users = jsonResponse.getJSONArray("data");

                        // Clear the table
                        tableModel.setRowCount(0);

                        // Populate the table with user data
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            tableModel.addRow(new String[]{
                                    String.valueOf(user.getInt("id")),
                                    user.getString("name"),
                                    user.getString("email"),
                                    user.getString("role")
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(UserForm.this, "No user data found in the API response.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(UserForm.this, "Error fetching users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();
    }

    // Fetch user by ID from API and update the table
    private void fetchUserById(String userId) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Fetch the response from the API
                    String url = GET_USER_BY_ID_URL.replace("{userId}", userId);
                    String response = sendAuthenticatedGetRequest(url);
                    System.out.println("API Response: " + response);

                    // Parse the response as a JSON object
                    JSONObject jsonResponse = new JSONObject(response);

                    // Check if the response contains the "data" field
                    if (jsonResponse.getString("message").equals("success")) {
                        JSONObject user = jsonResponse.getJSONObject("data");

                        // Clear the table
                        tableModel.setRowCount(0);

                        // Populate the table with the user's data
                        tableModel.addRow(new String[]{
                                String.valueOf(user.getInt("id")),
                                user.getString("name"),
                                user.getString("email"),
                                user.getString("role")
                        });
                    } else {
                        JOptionPane.showMessageDialog(UserForm.this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(UserForm.this, "Error fetching user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();
    }

    // Send authenticated GET request to API
    private String sendAuthenticatedGetRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            throw new IOException("HTTP Error: " + responseCode);
        }
    }

    // Send authenticated DELETE request to API
    private String sendAuthenticatedDeleteRequest(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            throw new IOException("HTTP Error: " + responseCode);
        }
    }

    // Add action listener
    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddUserForm addUserForm = new AddUserForm(jwtToken, UserForm.this);
            addUserForm.setVisible(true);
        }
    }

    // Update action listener
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(UserForm.this, "Please select a row to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = tableModel.getValueAt(selectedRow, 0).toString();
            UpdateUserForm updateUserForm = new UpdateUserForm(id, jwtToken, UserForm.this);
            updateUserForm.setVisible(true);
        }
    }

    // Delete action listener
    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(UserForm.this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the user ID from the selected row
            String userId = tableModel.getValueAt(selectedRow, 0).toString();

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(
                    UserForm.this,
                    "Are you sure you want to delete this user?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Send DELETE request to the API
                    String deleteUrl = DELETE_USER_URL.replace("{userId}", userId);
                    String response = sendAuthenticatedDeleteRequest(deleteUrl);

                    // Parse the delete response
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getString("message").equals("success")) {
                        JOptionPane.showMessageDialog(UserForm.this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(UserForm.this, "Error deleting user: " + jsonResponse.getString("data"), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(UserForm.this, "Error deleting user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Refresh action listener
    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTable();
        }
    }

    // Search action listener
    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = searchField.getText().trim();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(UserForm.this, "Please enter a user ID to search.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            fetchUserById(userId);
        }
    }

    // Open HR Form action listener
    private class OpenHRFormActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open HR Form and pass the JWT token
            HRForm hrForm = new HRForm(jwtToken);
            hrForm.setVisible(true);
            setVisible(false); // Hide the UserForm
        }
    }

    // Refresh table function
    public void refreshTable() {
        fetchAllUsers();
    }

    public static void main(String[] args) {
        // For testing purposes, you can pass a dummy JWT token
        SwingUtilities.invokeLater(() -> new UserForm("dummy-jwt-token"));
    }
}