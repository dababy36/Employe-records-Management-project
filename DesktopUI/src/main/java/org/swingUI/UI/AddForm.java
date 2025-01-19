package org.swingUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

class AddForm extends JFrame {
    private JTextField fullNameField, emailField, addressField, phoneNumberField, hireDateField, jobTitleField;
    private JComboBox<String> departmentComboBox; // Dropdown for department
    private JTextField employmentStatusField; // Non-editable field for status
    private String jwtToken;
    private HRForm hrForm; // Reference to HRForm

    public AddForm(String jwtToken, HRForm hrForm) {
        this.jwtToken = jwtToken;
        this.hrForm = hrForm;

        // Set up the form
        setTitle("Add Employee");
        setSize(400, 400);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Add fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Fetch department names and populate the JComboBox
        String[] departmentNames = fetchDepartmentNames();
        departmentComboBox = new JComboBox<>(departmentNames);
        departmentComboBox.setEditable(false); // Make the department dropdown non-editable

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Department:"), gbc);

        gbc.gridx = 1;
        add(departmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        phoneNumberField = new JTextField(20);
        add(phoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Hire Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        hireDateField = new JTextField(20);
        add(hireDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Job Title:"), gbc);

        gbc.gridx = 1;
        jobTitleField = new JTextField(20);
        add(jobTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("Employment Status:"), gbc);

        gbc.gridx = 1;
        employmentStatusField = new JTextField("ACTIVE", 20);
        employmentStatusField.setEditable(false); // Make the status field non-editable
        add(employmentStatusField, gbc);

        // Add submit button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect data from fields
                String fullName = fullNameField.getText();
                String email = emailField.getText();
                String department = (String) departmentComboBox.getSelectedItem(); // Get selected department
                String address = addressField.getText();
                String phoneNumber = phoneNumberField.getText();
                String hireDate = hireDateField.getText();
                String jobTitle = jobTitleField.getText();
                String employmentStatus = employmentStatusField.getText();

                // Create JSON payload
                String jsonPayload = String.format(
                        "{\"fullName\": \"%s\", \"email\": \"%s\", \"department\": \"%s\", \"address\": \"%s\", \"phoneNumber\": \"%s\", \"hireDate\": \"%s\", \"jobTitle\": \"%s\", \"employmentStatus\": \"%s\"}",
                        fullName, email, department, address, phoneNumber, hireDate, jobTitle, employmentStatus
                );

                try {
                    // Send POST request to the API
                    String addUrl = "http://  backendapp:8080/api/v1/employee/HR/add";
                    String response = sendAuthenticatedPostRequest(addUrl, jsonPayload);
                    System.out.println("Response: " + response);

                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    if (message.equals("success")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String employeeName = data.getString("fullName");
                        JOptionPane.showMessageDialog(AddForm.this, "Employee added successfully: " + employeeName, "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh the table in HRForm
                        hrForm.refreshTable();

                        // Close the form
                        dispose();
                    } else {
                        String data = jsonResponse.getString("data");
                        JOptionPane.showMessageDialog(AddForm.this, "Error adding employee: " + data, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(AddForm.this, "Error adding employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        add(submitButton, gbc);

        // Center the form on the screen
        setLocationRelativeTo(null);
    }

    // Fetch department names from API
    private String[] fetchDepartmentNames() {
        try {
            String apiUrl = "http://localhost:8080/api/v1/departement/all";
            String response = sendAuthenticatedGetRequest(apiUrl);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("message").equals("success")) {
                JSONArray departments = jsonResponse.getJSONArray("data");
                String[] departmentNames = new String[departments.length()];

                for (int i = 0; i < departments.length(); i++) {
                    JSONObject department = departments.getJSONObject(i);
                    departmentNames[i] = department.getString("name");
                }

                return departmentNames;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to fetch department names.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching department names: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return new String[0]; // Return an empty array if fetching fails
    }

    // Send authenticated GET request to API
    private String sendAuthenticatedGetRequest(String url) throws IOException {
        System.out.println("Sending GET request to: " + url); // Log the request URL
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken); // Add JWT token to header

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode); // Log the response code

        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            // Handle error response
            Scanner scanner = new Scanner(connection.getErrorStream());
            StringBuilder errorResponse = new StringBuilder();
            while (scanner.hasNext()) {
                errorResponse.append(scanner.nextLine());
            }
            scanner.close();
            System.out.println("Error Response: " + errorResponse.toString()); // Log the error response
            throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
        }
    }

    // Send authenticated POST request to API
    private String sendAuthenticatedPostRequest(String url, String jsonPayload) throws IOException {
        System.out.println("Sending POST request to: " + url); // Log the request URL
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken); // Add JWT token to header
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the JSON payload
        connection.getOutputStream().write(jsonPayload.getBytes());

        int responseCode = connection.getResponseCode();
        System.out.println("POST Response Code: " + responseCode); // Log the response code

        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            // Handle error response
            Scanner scanner = new Scanner(connection.getErrorStream());
            StringBuilder errorResponse = new StringBuilder();
            while (scanner.hasNext()) {
                errorResponse.append(scanner.nextLine());
            }
            scanner.close();
            System.out.println("Error Response: " + errorResponse.toString()); // Log the error response
            throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
        }
    }
}