package org.swingUI.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManagerForm extends JFrame {

    // Table and model
    private JTable table;
    private DefaultTableModel tableModel;

    // Filter fields
    private JComboBox<String> statusComboBox; // Dropdown for status
    private JTextField startDateField, endDateField;

    // Buttons
    private JButton filterButton, updateButton, refreshButton;

    // API URLs
    private static final String GET_EMPLOYEES_BY_DEPARTMENT_URL = "http://localhost:8080/api/v1/employee/Manager/employe/by-department";
    private static final String FILTER_API_URL = "http://localhost:8080/api/v1/employee/MANAGER/filterUsers";

    // JWT Token and department
    private String jwtToken;
    private String department;

    // Constructor to accept JWT token and department
    public ManagerForm(String jwtToken, String department) {
        this.jwtToken = jwtToken;
        this.department = department;

        // Set up the frame
        setTitle("Manager Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE instead of EXIT_ON_CLOSE
        setSize(1200, 700); // Increased size for better spacing
        setLayout(new BorderLayout());

        // Add a window listener to redirect to LoginForm when the form is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the LoginForm when ManagerForm is closed
                new LoginForm().setVisible(true);
            }
        });

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(0, 102, 204)); // Blue background
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Manager " + department + " Space");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE); // White text
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        // Add filter panel
        JPanel filterPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        filterPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Status Dropdown
        String[] statusOptions = {"ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "FIRED"}; // Predefined status options
        statusComboBox = new JComboBox<>(statusOptions);

        startDateField = new JTextField();
        endDateField = new JTextField();
        filterButton = new JButton("Filter");

        // Style buttons with blue background and white text
        styleButton(filterButton);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusComboBox); // Use dropdown for status
        filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        filterPanel.add(endDateField);
        filterPanel.add(filterButton);

        // Add welcome panel and filter panel to the top of the frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(welcomePanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        add(topPanel, BorderLayout.NORTH);

        // Create table with updated columns
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Full Name", "Email", "Job Title", "Employment Status", "Phone Number", "Address", "Hire Date"
        }, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Serif", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        updateButton = new JButton("Update");
        refreshButton = new JButton("Refresh Table");

        // Style buttons with blue background and white text
        styleButton(updateButton);
        styleButton(refreshButton);

        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listeners
        filterButton.addActionListener(new FilterActionListener());
        updateButton.addActionListener(new UpdateActionListener());
        refreshButton.addActionListener(new RefreshActionListener());

        // Fetch initial data automatically with JWT authentication
        refreshTable();

        // Center the frame on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method to style buttons with blue background and white text
    private void styleButton(JButton button) {
        if (button != null) {
            button.setFont(new Font("Serif", Font.BOLD, 14));
            button.setBackground(Color.BLUE); // Blue background
            button.setForeground(Color.BLACK); // White text
            button.setFocusPainted(false); // Remove focus border
        }
    }

    // Fetch employees by department (handles JSON array response)
    private void fetchEmployeesByDepartment() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Fetch the response from the API
                    String response = sendAuthenticatedGetRequest(GET_EMPLOYEES_BY_DEPARTMENT_URL);
                    System.out.println("API Response: " + response);

                    // Parse the response as a JSON array
                    JSONArray employees = new JSONArray(response);

                    // Clear the table
                    tableModel.setRowCount(0);

                    // Populate the table with employee data
                    for (int i = 0; i < employees.length(); i++) {
                        JSONObject employee = employees.getJSONObject(i);
                        tableModel.addRow(new String[]{
                                String.valueOf(employee.getInt("id")),
                                employee.getString("fullName"),
                                employee.getString("email"),
                                employee.getString("jobTitle"),
                                employee.getString("employementStatus"),
                                employee.getString("phoneNumber"),
                                employee.getString("address"),
                                employee.getString("hireDate")
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManagerForm.this, "Error fetching employees by department: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();
    }

    // Fetch filtered employees (handles JSON object response with "data" field)
    private void fetchFilteredEmployees(String filterUrl) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Fetch the response from the API
                    String response = sendAuthenticatedGetRequest(filterUrl);
                    System.out.println("API Response: " + response);

                    // Parse the response as a JSON object
                    JSONObject jsonResponse = new JSONObject(response);

                    // Check if the response contains the "data" field
                    if (jsonResponse.has("data")) {
                        // Extract the "data" field as a JSON array
                        JSONArray employees = jsonResponse.getJSONArray("data");

                        // Clear the table
                        tableModel.setRowCount(0);

                        // Populate the table with filtered employee data
                        for (int i = 0; i < employees.length(); i++) {
                            JSONObject employee = employees.getJSONObject(i);
                            tableModel.addRow(new String[]{
                                    String.valueOf(employee.getInt("id")),
                                    employee.getString("fullName"),
                                    employee.getString("email"),
                                    employee.getString("jobTitle"),
                                    employee.getString("employementStatus"),
                                    employee.getString("phoneNumber"),
                                    employee.getString("address"),
                                    employee.getString("hireDate")
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(ManagerForm.this, "No employee data found in the API response.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ManagerForm.this, "Error fetching filtered employees: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    // Filter action listener
    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String status = (String) statusComboBox.getSelectedItem(); // Get selected status
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();

                if (status.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    JOptionPane.showMessageDialog(ManagerForm.this, "Please fill all filter fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Construct the filter URL
                String filterUrl = FILTER_API_URL + "?employementStatus=" + URLEncoder.encode(status, "UTF-8") +
                        "&startDate=" + URLEncoder.encode(startDate, "UTF-8") +
                        "&endDate=" + URLEncoder.encode(endDate, "UTF-8");

                // Fetch the filtered data from the API
                fetchFilteredEmployees(filterUrl);
            } catch (UnsupportedEncodingException ex) {
                JOptionPane.showMessageDialog(ManagerForm.this, "Error encoding filter parameters: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Update action listener
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ManagerForm.this, "Please select a row to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = tableModel.getValueAt(selectedRow, 0).toString();
            UpdateManagerForm updateForm = new UpdateManagerForm(id, jwtToken, ManagerForm.this);
            updateForm.setVisible(true);
        }
    }

    // Refresh action listener
    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshTable();
        }
    }

    // Refresh table function
    public void refreshTable() {
        fetchEmployeesByDepartment();
    }

    public static void main(String[] args) {
        // For testing purposes, you can pass a dummy JWT token and department
        SwingUtilities.invokeLater(() -> new ManagerForm("dummy-jwt-token", "IT Department"));
    }
}