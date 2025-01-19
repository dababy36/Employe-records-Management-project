package org.swingUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddUserForm extends JFrame {

    // Fields
    private JTextField nameField, emailField, passwordField;
    private JComboBox<String> roleComboBox, departmentComboBox;
    private JButton addButton;

    // API URLs
    private static final String CREATE_USER_URL = "http://localhost:8080/api/v1/user/ADMIN/user/create";
    private static final String GET_DEPARTMENTS_URL = "http://localhost:8080/api/v1/departement/all";

    // JWT Token
    private String jwtToken;
    private UserForm userForm;

    // Constructor to accept JWT token and UserForm reference
    public AddUserForm(String jwtToken, UserForm userForm) {
        this.jwtToken = jwtToken;
        this.userForm = userForm;

        // Set up the frame
        setTitle("Add User");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Add padding
        setLocationRelativeTo(null); // Center the frame

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(51, 51, 51)); // Dark gray text
        nameField = new JTextField();
        styleTextField(nameField);
        mainPanel.add(nameLabel);
        mainPanel.add(nameField);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(new Color(51, 51, 51));
        emailField = new JTextField();
        styleTextField(emailField);
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);

        // Department dropdown
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        departmentLabel.setForeground(new Color(51, 51, 51));
        departmentComboBox = new JComboBox<>();
        styleComboBox(departmentComboBox);
        fetchDepartments(); // Fetch and populate department names
        mainPanel.add(departmentLabel);
        mainPanel.add(departmentComboBox);

        // Role dropdown
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(new Color(51, 51, 51));
        String[] roles = {"ADMIN", "MANAGER", "HR"}; // Add other roles if needed
        roleComboBox = new JComboBox<>(roles);
        styleComboBox(roleComboBox);
        mainPanel.add(roleLabel);
        mainPanel.add(roleComboBox);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(51, 51, 51));
        passwordField = new JTextField();
        styleTextField(passwordField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);

        // Add button
        addButton = new JButton("Add User");
        styleButton(addButton); // Apply custom styling

        // Add components to the frame
        add(mainPanel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);

        // Attach action listener
        addButton.addActionListener(new AddActionListener());

        // Make the frame visible
        setVisible(true);
    }

    // Style text fields
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);
        textField.setForeground(new Color(51, 51, 51));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Add padding
        ));
    }

    // Style combo boxes
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(51, 51, 51));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Add padding
        ));
    }

    // Style the add button
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        button.setFocusPainted(false); // Remove focus border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 86, 179)); // Darker blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Restore original color
            }
        });
    }

    // Fetch departments from API and populate the dropdown
    private void fetchDepartments() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Fetch the response from the API
                    String response = sendAuthenticatedGetRequest(GET_DEPARTMENTS_URL);
                    System.out.println("API Response: " + response);

                    // Parse the response as a JSON object
                    JSONObject jsonResponse = new JSONObject(response);

                    // Check if the response contains the "data" field
                    if (jsonResponse.has("data")) {
                        // Extract the "data" field as a JSON array
                        JSONArray departments = jsonResponse.getJSONArray("data");

                        // Extract department names
                        List<String> departmentNames = new ArrayList<>();
                        for (int i = 0; i < departments.length(); i++) {
                            JSONObject department = departments.getJSONObject(i);
                            departmentNames.add(department.getString("name"));
                        }

                        // Populate the department dropdown
                        departmentComboBox.setModel(new DefaultComboBoxModel<>(departmentNames.toArray(new String[0])));
                    } else {
                        JOptionPane.showMessageDialog(AddUserForm.this, "No department data found in the API response.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AddUserForm.this, "Error fetching departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // Send authenticated POST request to API
    private String sendAuthenticatedPostRequest(String url, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(body.getBytes());

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
            try {
                // Validate input fields
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String department = (String) departmentComboBox.getSelectedItem();
                String password = passwordField.getText().trim();
                String role = (String) roleComboBox.getSelectedItem();

                if (name.isEmpty() || email.isEmpty() || department.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(AddUserForm.this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create JSON payload
                JSONObject user = new JSONObject();
                user.put("name", name);
                user.put("email", email);
                user.put("department", department);
                user.put("password", password);
                user.put("role", role);

                // Send POST request to create user
                String response = sendAuthenticatedPostRequest(CREATE_USER_URL, user.toString());

                // Parse the response
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getString("message").equals("success")) {
                    JOptionPane.showMessageDialog(AddUserForm.this, "User created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    userForm.refreshTable(); // Refresh the user list in the parent form
                    dispose(); // Close the form
                } else {
                    JOptionPane.showMessageDialog(AddUserForm.this, "Error creating user: " + jsonResponse.getString("data"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AddUserForm.this, "Error creating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}