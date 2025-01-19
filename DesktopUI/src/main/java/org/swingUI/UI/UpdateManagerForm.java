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

public class UpdateManagerForm extends JFrame {

    private JTextField jobTitleField;
    private JComboBox<String> statusComboBox; // Dropdown for status
    private JButton updateButton;
    private String jwtToken;
    private String userId;
    private ManagerForm managerForm;

    public UpdateManagerForm(String userId, String jwtToken, ManagerForm managerForm) {
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.managerForm = managerForm;

        // Set up the frame
        setTitle("Update Employee");
        setSize(400, 200); // Increased size for better layout
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10)); // Add spacing between components
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Job Title Field
        jobTitleField = new JTextField();
        mainPanel.add(new JLabel("Job Title:"));
        mainPanel.add(jobTitleField);

        // Status Dropdown
        String[] statusOptions = {"ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "FIRED"}; // Predefined status options
        statusComboBox = new JComboBox<>(statusOptions);
        mainPanel.add(new JLabel("Status:"));
        mainPanel.add(statusComboBox);

        // Update Button
        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0, 102, 204)); // Blue background
        updateButton.setForeground(Color.WHITE); // White text
        updateButton.setFont(new Font("Serif", Font.BOLD, 14));

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Add the button to a separate panel for better alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listener to the update button
        updateButton.addActionListener(new UpdateActionListener());

        // Center the form on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Create JSON payload with updated data
                JSONObject employee = new JSONObject();
                employee.put("jobTitle", jobTitleField.getText());
                employee.put("employementStatus", statusComboBox.getSelectedItem()); // Get selected status

                // Send PATCH request to update the employee
                String response = sendAuthenticatedPatchRequest("http://localhost:8080/api/v1/employee/Manager/" + userId + "/update", employee.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getString("message").equals("success")) {
                    JOptionPane.showMessageDialog(UpdateManagerForm.this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    managerForm.refreshTable(); // Refresh the table in the ManagerForm
                    dispose(); // Close the update form
                } else {
                    JOptionPane.showMessageDialog(UpdateManagerForm.this, "Error updating employee: " + jsonResponse.getString("data"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(UpdateManagerForm.this, "Error updating employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String sendAuthenticatedPatchRequest(String url, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON payload to the request body
        connection.getOutputStream().write(body.getBytes());

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Read the response
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
}