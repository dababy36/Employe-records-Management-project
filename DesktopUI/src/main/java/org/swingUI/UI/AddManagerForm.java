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

public class AddManagerForm extends JFrame {

    private JTextField fullNameField, emailField, jobTitleField, statusField, phoneField, addressField, hireDateField;
    private JButton addButton;
    private String jwtToken;
    private ManagerForm managerForm;

    public AddManagerForm(String jwtToken, ManagerForm managerForm) {
        this.jwtToken = jwtToken;
        this.managerForm = managerForm;

        setTitle("Add Employee");
        setSize(400, 300);
        setLayout(new GridLayout(8, 2));

        fullNameField = new JTextField();
        emailField = new JTextField();
        jobTitleField = new JTextField();
        statusField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        hireDateField = new JTextField();
        addButton = new JButton("Add");

        add(new JLabel("Full Name:"));
        add(fullNameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Job Title:"));
        add(jobTitleField);
        add(new JLabel("Status:"));
        add(statusField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("Hire Date (YYYY-MM-DD):"));
        add(hireDateField);
        add(addButton);

        addButton.addActionListener(new AddActionListener());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JSONObject employee = new JSONObject();
                employee.put("fullName", fullNameField.getText());
                employee.put("email", emailField.getText());
                employee.put("jobTitle", jobTitleField.getText());
                employee.put("employementStatus", statusField.getText());
                employee.put("phoneNumber", phoneField.getText());
                employee.put("address", addressField.getText());
                employee.put("hireDate", hireDateField.getText());

                String response = sendAuthenticatedPostRequest("http://localhost:8080/api/Manager/add", employee.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getString("message").equals("success")) {
                    JOptionPane.showMessageDialog(AddManagerForm.this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    managerForm.refreshTable();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(AddManagerForm.this, "Error adding employee: " + jsonResponse.getString("data"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AddManagerForm.this, "Error adding employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
}