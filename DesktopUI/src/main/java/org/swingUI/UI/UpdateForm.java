package org.swingUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class UpdateForm extends JFrame {
    private JTextField fullNameField, emailField, addressField, phoneNumberField, jobTitleField;
    private JComboBox<String> departmentComboBox, employmentStatusComboBox;
    private String jwtToken;
    private String userId;
    private HRForm hrForm; // Reference to HRForm

    public UpdateForm(String userId, String fullName, String email, String jobTitle, String employmentStatus, String phoneNumber, String address, String jwtToken, HRForm hrForm) {
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.hrForm = hrForm; // Store the reference

        // Set up the form
        setTitle("Update Employee");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the form on the screen

        // Create a main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components
        gbc.anchor = GridBagConstraints.WEST;

        // Add fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fullNameField = new JTextField(fullName, 20);
        mainPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        emailField = new JTextField(email, 20);
        mainPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Department:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        departmentComboBox = new JComboBox<>();
        fetchDepartments(); // Fetch and populate department names
        mainPanel.add(departmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        addressField = new JTextField(address, 20);
        mainPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        phoneNumberField = new JTextField(phoneNumber, 20);
        mainPanel.add(phoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Job Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        jobTitleField = new JTextField(jobTitle, 20);
        mainPanel.add(jobTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Employment Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        employmentStatusComboBox = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "FIRED"});
        employmentStatusComboBox.setSelectedItem(employmentStatus); // Set the current status
        mainPanel.add(employmentStatusComboBox, gbc);

        // Add submit button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 102, 204)); // Blue background
        submitButton.setForeground(Color.WHITE); // White text
        submitButton.setFont(new Font("Serif", Font.BOLD, 14));
        submitButton.setFocusPainted(false); // Remove focus border
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect data from fields
                String updatedFullName = fullNameField.getText();
                String updatedEmail = emailField.getText();
                String updatedDepartment = (String) departmentComboBox.getSelectedItem();
                String updatedAddress = addressField.getText();
                String updatedPhoneNumber = phoneNumberField.getText();
                String updatedJobTitle = jobTitleField.getText();
                String updatedEmploymentStatus = (String) employmentStatusComboBox.getSelectedItem();

                // Create JSON payload
                String jsonPayload = String.format(
                        "{\"fullName\": \"%s\", \"email\": \"%s\", \"department\": \"%s\", \"address\": \"%s\", \"phoneNumber\": \"%s\", \"jobTitle\": \"%s\", \"employementStatus\": \"%s\"}",
                        updatedFullName, updatedEmail, updatedDepartment, updatedAddress, updatedPhoneNumber, updatedJobTitle, updatedEmploymentStatus
                );

                try {
                    // Send PUT request to the API
                    String updateUrl = "http://localhost:8080/api/v1/employee/HR/" + userId + "/update";
                    String response = sendAuthenticatedPutRequest(updateUrl, jsonPayload);

                    // Parse the response
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    if ("success".equals(message)) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String employeeName = data.getString("fullName");
                        JOptionPane.showMessageDialog(UpdateForm.this, "Employee updated successfully: " + employeeName, "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh the table in HRForm
                        hrForm.refreshTable();

                        // Close the form
                        dispose();
                    } else {
                        String data = jsonResponse.getString("data");
                        JOptionPane.showMessageDialog(UpdateForm.this, "Error updating employee: " + data, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(UpdateForm.this, "Error updating employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (JSONException ex) {
                    JOptionPane.showMessageDialog(UpdateForm.this, "Error parsing JSON response: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        mainPanel.add(submitButton, gbc);

        // Add main panel to the frame
        add(mainPanel, BorderLayout.CENTER);
    }

    // Fetch department names from the API and populate the combo box
    private void fetchDepartments() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String apiUrl = "http://loalhost:8080/api/v1/departement/all";
                    String response = sendAuthenticatedGetRequest(apiUrl);
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.getString("message").equals("success")) {
                        JSONArray departments = jsonResponse.getJSONArray("data");

                        // Populate the combo box with department names
                        for (int i = 0; i < departments.length(); i++) {
                            JSONObject department = departments.getJSONObject(i);
                            String departmentName = department.getString("name");
                            departmentComboBox.addItem(departmentName);
                        }
                    } else {
                        JOptionPane.showMessageDialog(UpdateForm.this, "Failed to fetch department names.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(UpdateForm.this, "Error fetching department names: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream)) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                return response.toString();
            }
        } else {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream == null) {
                throw new IOException("HTTP Error: " + responseCode + " - No error response from server.");
            }
            try (Scanner scanner = new Scanner(errorStream)) {
                StringBuilder errorResponse = new StringBuilder();
                while (scanner.hasNext()) {
                    errorResponse.append(scanner.nextLine());
                }
                throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
            }
        }
    }

    // Send authenticated PUT request to API
    private String sendAuthenticatedPutRequest(String url, String jsonPayload) throws IOException {
        System.out.println("Sending PUT request to: " + url);
        System.out.println("Authorization Header: Bearer " + jwtToken);
        System.out.println("JSON Payload: " + jsonPayload);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the JSON payload
        connection.getOutputStream().write(jsonPayload.getBytes());

        int responseCode = connection.getResponseCode();
        System.out.println("PUT Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream)) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                System.out.println("API Response: " + response.toString());
                return response.toString();
            }
        } else {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream == null) {
                System.out.println("No error response from server.");
                throw new IOException("HTTP Error: " + responseCode + " - No error response from server.");
            }
            try (Scanner scanner = new Scanner(errorStream)) {
                StringBuilder errorResponse = new StringBuilder();
                while (scanner.hasNext()) {
                    errorResponse.append(scanner.nextLine());
                }
                System.out.println("Error Response: " + errorResponse.toString());
                throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
            }
        }
    }
}