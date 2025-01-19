package org.swingUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import net.miginfocom.swing.MigLayout;
import org.json.JSONObject;

public class LoginForm extends JFrame {

    // Fields to store email and password
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginForm() {
        // Set up the frame
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350); // Slightly larger for better spacing
        setLayout(new MigLayout("wrap 2", "[grow, fill][grow, fill]", "[]20[]20[]20[]"));

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add a title label
        JLabel titleLabel = new JLabel("Login to Employee Management System");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204)); // Blue color
        add(titleLabel, "span, align center, gapbottom 20");

        // Add email label and field
        add(new JLabel("Email:"), "align right");
        emailField = new JTextField(20);
        emailField.setFont(new Font("Serif", Font.PLAIN, 14));
        add(emailField, "growx");

        // Add password label and field
        add(new JLabel("Password:"), "align right");
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Serif", Font.PLAIN, 14));
        add(passwordField, "growx");

        // Add login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Serif", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204)); // Blue background
        loginButton.setForeground(Color.black); // White text
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.setPreferredSize(new Dimension(100, 40)); // Set button size
        add(loginButton, "span, align center, gaptop 20");

        // Add status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        add(statusLabel, "span, align center, gaptop 10");

        // Attach action listener to the login button
        loginButton.addActionListener(new LoginActionListener());

        // Center the frame on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ActionListener for handling login button click
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Email and Password cannot be empty!");
                return;
            }

            try {
                // Send API request
                String apiResponse = sendLoginRequest(email, password);
                System.out.println("API Response: " + apiResponse);

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(apiResponse);

                // Check if the login was successful
                if (jsonResponse.getString("message").equals("success")) {
                    // Extract token, role, and department
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String token = data.getString("token");
                    String role = data.getString("role");
                    String department = data.getString("department");

                    // Update UI for success
                    statusLabel.setForeground(new Color(0, 153, 51)); // Green color
                    statusLabel.setText("Login successful! Role: " + role);

                    // Open the appropriate form based on the user's role
                    SwingUtilities.invokeLater(() -> {
                        switch (role.toUpperCase()) {
                            case "ADMIN":
                                new UserForm(token); // Open UserForm for admin
                                break;
                            case "HR":
                                new HRForm(token); // Open HRForm for HR
                                break;
                            case "MANAGER":
                                new ManagerForm(token, department); // Open ManagerForm for manager
                                break;
                            default:
                                JOptionPane.showMessageDialog(LoginForm.this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                        }
                        dispose(); // Close the LoginForm
                    });
                } else {
                    // Handle error case
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Login failed. Check credentials.");
                }

            } catch (IOException ex) {
                statusLabel.setText("Login failed. Check credentials.");
                ex.printStackTrace();
            } catch (Exception ex) {
                statusLabel.setText("Error parsing API response.");
                ex.printStackTrace();
            }
        }
    }

    // Method to send a login request to the API
    private String sendLoginRequest(String email, String password) throws IOException {
        // Replace with your actual API URL
        String apiUrl = "http://localhost:8080/api/auth/authenticate";

        // Set up the connection
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Create JSON payload
        String jsonPayload = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        // Send the request
        connection.getOutputStream().write(jsonPayload.getBytes());

        // Read the response
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder responseBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            responseBuilder.append(scanner.nextLine());
        }
        scanner.close();

        return responseBuilder.toString();
    }

    public static void main(String[] args) {
        // Run the login page
        SwingUtilities.invokeLater(LoginForm::new);
    }
}