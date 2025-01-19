package org.swingUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class UpdateUserForm extends JFrame {

    // Fields
    private JComboBox<String> roleComboBox;
    private JButton updateButton;

    // API URL
    private static final String UPDATE_USER_URL = "http://localhost:8080/api/v1/user/ADMIN/user/{userId}/update";

    // JWT Token
    private String jwtToken;
    private String userId;
    private UserForm userForm;

    // Constructor to accept user ID, JWT token, and UserForm reference
    public UpdateUserForm(String userId, String jwtToken, UserForm userForm) {
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.userForm = userForm;

        // Set up the frame
        setTitle("Update User Role");
        setSize(400, 200);
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
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        mainPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Role dropdown panel
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setBackground(new Color(245, 245, 245));
        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(new Color(51, 51, 51)); // Dark gray text
        rolePanel.add(roleLabel);

        String[] roles = {"ADMIN", "MANAGER", "HR"}; // Add other roles if needed
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(new Color(51, 51, 51));
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Add padding
        ));
        rolePanel.add(roleComboBox);

        // Update button
        updateButton = new JButton("Update Role");
        styleButton(updateButton); // Apply custom styling

        // Add components to the main panel
        mainPanel.add(rolePanel);
        mainPanel.add(updateButton);

        // Add main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Attach action listener
        updateButton.addActionListener(new UpdateActionListener());

        // Make the frame visible
        setVisible(true);
    }

    // Style the update button
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

    // Send authenticated PUT request to API
    private String sendAuthenticatedPutRequest(String url, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("PUT");
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

    // Update action listener
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get the selected role
                String role = (String) roleComboBox.getSelectedItem();

                // Create JSON payload
                JSONObject payload = new JSONObject();
                payload.put("role", role);

                // Send PUT request to update user role
                String updateUrl = UPDATE_USER_URL.replace("{userId}", userId);
                String response = sendAuthenticatedPutRequest(updateUrl, payload.toString());

                // Parse the response
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getString("message").equals("success")) {
                    JOptionPane.showMessageDialog(UpdateUserForm.this, "User role updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    userForm.refreshTable(); // Refresh the user list in the parent form
                    dispose(); // Close the form
                } else {
                    JOptionPane.showMessageDialog(UpdateUserForm.this, "Error updating user role: " + jsonResponse.getString("data"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(UpdateUserForm.this, "Error updating user role: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}