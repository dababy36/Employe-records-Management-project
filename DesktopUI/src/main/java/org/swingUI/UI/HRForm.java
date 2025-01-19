package org.swingUI.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HRForm extends JFrame {

    // Table and model
    private JTable table;
    private DefaultTableModel tableModel;

    // Filter fields
    private JComboBox<String> statusComboBox; // Dropdown for status
    private JTextField startDateField, endDateField;
    private JComboBox<String> departmentComboBox;

    // Search fields
    private JTextField searchField; // Field for searching by ID
    private JButton searchButton; // Button to trigger search

    // Buttons
    private JButton filterButton, updateButton, deleteButton, addButton, refreshButton, exportRapportButton;

    // API URL
    private static final String API_URL = "http://localhost:8080/api/v1/employee/HR/all";
    private static final String FILTER_API_URL = "http://localhost:8080/api/v1/employee/filterUsers";

    // JWT Token
    private String jwtToken;

    // Constructor to accept JWT token
    public HRForm(String jwtToken) {
        this.jwtToken = jwtToken;

        // Set up the frame
        setTitle("Employee Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Change to DISPOSE_ON_CLOSE
        setSize(1200, 700); // Increased width to accommodate more columns
        setLayout(new BorderLayout());

        // Add a window listener to return to the login form when HRForm is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                new LoginForm().setVisible(true); // Open the login form
            }
        });

        // Create a welcome message panel
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setBackground(new Color(0, 102, 204)); // Blue background
        JLabel welcomeLabel = new JLabel("Welcome to HR Space");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE); // White text
        welcomePanel.add(welcomeLabel);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10)); // Add more padding to the welcome panel

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        searchField = new JTextField(15); // Search field for employee ID
        searchButton = new JButton("Search by ID"); // Search button
        searchButton.setBackground(new Color(0, 102, 204)); // Blue background
        searchButton.setForeground(Color.BLACK); // White text
        searchButton.setFont(new Font("Serif", Font.BOLD, 14));

        searchPanel.add(new JLabel("Search by ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create filter panel
        JPanel filterPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        filterPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Add status dropdown with all options
        String[] statusOptions = {"ACTIVE", "INACTIVE", "ON_LEAVE", "SUSPENDED", "FIRED"}; // All status options
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setBackground(Color.WHITE);

        startDateField = new JTextField();
        endDateField = new JTextField();
        filterButton = new JButton("Filter");
        filterButton.setBackground(new Color(0, 102, 204)); // Blue background
        filterButton.setForeground(Color.BLACK); // White text
        filterButton.setFont(new Font("Serif", Font.BOLD, 14));

        // Fetch department names and populate the JComboBox
        String[] departmentNames = fetchDepartmentNames();
        departmentComboBox = new JComboBox<>(departmentNames);
        departmentComboBox.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusComboBox); // Use dropdown for status
        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(departmentComboBox);
        filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        filterPanel.add(endDateField);
        filterPanel.add(filterButton);

        // Create a panel to hold the welcome message, search panel, and filter panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical arrangement
        topPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Add components to the top panel
        topPanel.add(welcomePanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical space
        topPanel.add(searchPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical space
        topPanel.add(filterPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Add padding

        add(topPanel, BorderLayout.NORTH);

        // Create table with updated columns (including department)
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Full Name", "Email", "Job Title", "Employment Status", "Phone Number", "Address", "Hire Date", "Department"
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
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        addButton = new JButton("Add");
        refreshButton = new JButton("Refresh Table");
        exportRapportButton = new JButton("Export Rapport");

        // Set button styles
        updateButton.setBackground(new Color(0, 102, 204)); // Blue background
        updateButton.setForeground(Color.BLACK); // White text
        updateButton.setFont(new Font("Serif", Font.BOLD, 14));

        deleteButton.setBackground(new Color(204, 0, 0)); // Red background
        deleteButton.setForeground(Color.BLACK); // White text
        deleteButton.setFont(new Font("Serif", Font.BOLD, 14));

        addButton.setBackground(new Color(0, 153, 51)); // Green background
        addButton.setForeground(Color.BLACK); // White text
        addButton.setFont(new Font("Serif", Font.BOLD, 14));

        refreshButton.setBackground(new Color(0, 102, 204)); // Blue background
        refreshButton.setForeground(Color.BLACK); // White text
        refreshButton.setFont(new Font("Serif", Font.BOLD, 14));

        exportRapportButton.setBackground(new Color(0, 102, 204)); // Blue background
        exportRapportButton.setForeground(Color.WHITE); // White text
        exportRapportButton.setFont(new Font("Serif", Font.BOLD, 14));

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportRapportButton); // Add Export Rapport button

        add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listeners
        filterButton.addActionListener(new FilterActionListener());
        updateButton.addActionListener(new UpdateActionListener());
        deleteButton.addActionListener(new DeleteActionListener());
        addButton.addActionListener(new AddActionListener());
        refreshButton.addActionListener(new RefreshActionListener());
        exportRapportButton.addActionListener(new ExportRapportActionListener());
        searchButton.addActionListener(new SearchActionListener()); // Add action listener for search button

        // Fetch initial data automatically with JWT authentication
        fetchDataFromAPI(API_URL);

        // Center the frame on the screen
        setLocationRelativeTo(null);
        setVisible(true);
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

    // Fetch data from API and populate the table
    private void fetchDataFromAPI(String url) {
        try {
            System.out.println("Fetching data from API: " + url); // Log the API URL
            String response = sendAuthenticatedGetRequest(url);
            System.out.println("API Response: " + response); // Log the raw API response

            // Parse the JSON object
            JSONObject jsonResponse = new JSONObject(response);

            // Check if the response contains the "data" field
            if (jsonResponse.has("data")) {
                JSONArray employees = jsonResponse.getJSONArray("data"); // Extract the "data" array
                System.out.println("Number of employees fetched: " + employees.length()); // Log the number of employees

                // Clear the table
                tableModel.setRowCount(0);

                // Populate the table
                for (int i = 0; i < employees.length(); i++) {
                    JSONObject employee = employees.getJSONObject(i);
                    System.out.println("Employee " + (i + 1) + ": " + employee); // Log each employee object

                    // Handle the "id" field as an integer
                    int id = employee.getInt("id");

                    // Extract department information
                    JSONObject department = employee.getJSONObject("departement");
                    String departmentName = department.getString("name");

                    tableModel.addRow(new String[]{
                            String.valueOf(id), // Convert id to string
                            employee.getString("fullName"),
                            employee.getString("email"),
                            employee.getString("jobTitle"),
                            employee.getString("employementStatus"),
                            employee.getString("phoneNumber"),
                            employee.getString("address"),
                            employee.getString("hireDate"),
                            departmentName // Add department name
                    });
                }
            } else {
                System.out.println("No 'data' field found in the API response.");
                JOptionPane.showMessageDialog(this, "No employee data found in the API response.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace
            JOptionPane.showMessageDialog(this, "Error fetching data from API: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    // Search action listener
    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = searchField.getText().trim();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(HRForm.this, "Please enter an employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Create the URL for the search API
                String searchUrl = "http://localhost:8080/api/v1/employee/" + userId + "/getEmploye";

                // Send a GET request to the search API
                String response = sendAuthenticatedGetRequest(searchUrl);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getString("message").equals("success")) {
                    // Clear the table
                    tableModel.setRowCount(0);

                    // Get the employee data
                    JSONObject employee = jsonResponse.getJSONObject("data");

                    // Extract department information
                    JSONObject department = employee.getJSONObject("departement");
                    String departmentName = department.getString("name");

                    // Add the employee to the table
                    tableModel.addRow(new Object[]{
                            String.valueOf(employee.getInt("id")), // Convert id to string
                            employee.getString("fullName"),
                            employee.getString("email"),
                            employee.getString("jobTitle"),
                            employee.getString("employementStatus"),
                            employee.getString("phoneNumber"),
                            employee.getString("address"),
                            employee.getString("hireDate"),
                            departmentName
                    });
                } else {
                    JOptionPane.showMessageDialog(HRForm.this, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(HRForm.this, "Error searching for employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JSONException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(HRForm.this, "Error parsing employee data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Filter action listener
    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get filter values from the input fields
                String status = (String) statusComboBox.getSelectedItem(); // Get selected status
                String department = (String) departmentComboBox.getSelectedItem();
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();

                // Validate the input fields
                if (status.isEmpty() || department.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    JOptionPane.showMessageDialog(HRForm.this, "Please fill all filter fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // URL-encode the filter parameters
                String encodedStatus = URLEncoder.encode(status, "UTF-8");
                String encodedDepartment = URLEncoder.encode(department, "UTF-8");
                String encodedStartDate = URLEncoder.encode(startDate, "UTF-8");
                String encodedEndDate = URLEncoder.encode(endDate, "UTF-8");

                // Construct the filter URL with encoded parameters
                String filterUrl = FILTER_API_URL + "?employementStatus=" + encodedStatus +
                        "&department=" + encodedDepartment +
                        "&startDate=" + encodedStartDate +
                        "&endDate=" + encodedEndDate;

                // Fetch data from the API with the filter parameters
                fetchDataFromAPI(filterUrl);
            } catch (UnsupportedEncodingException ex) {
                JOptionPane.showMessageDialog(HRForm.this, "Error encoding filter parameters: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Update action listener
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(HRForm.this, "Please select a row to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the employee details from the selected row
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String fullName = tableModel.getValueAt(selectedRow, 1).toString();
            String email = tableModel.getValueAt(selectedRow, 2).toString();
            String jobTitle = tableModel.getValueAt(selectedRow, 3).toString();
            String employmentStatus = tableModel.getValueAt(selectedRow, 4).toString();
            String phoneNumber = tableModel.getValueAt(selectedRow, 5).toString();
            String address = tableModel.getValueAt(selectedRow, 6).toString();

            // Open update form and pass employee details
            UpdateForm updateForm = new UpdateForm(id, fullName, email, jobTitle, employmentStatus, phoneNumber, address, jwtToken, HRForm.this);
            updateForm.setVisible(true);
        }
    }

    // Delete action listener
    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(HRForm.this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the employee ID from the selected row
            String id = tableModel.getValueAt(selectedRow, 0).toString();

            try {
                // Send DELETE request to the API
                String deleteUrl = "http://localhost:8080/api/v1/employee/HR/" + id + "/delete";
                String deleteResponse = sendAuthenticatedDeleteRequest(deleteUrl);

                // Parse the delete response
                JSONObject jsonResponse = new JSONObject(deleteResponse);
                String message = jsonResponse.getString("message");
                String data = jsonResponse.getString("data");

                if (message.equals("success")) {
                    // Show success message
                    JOptionPane.showMessageDialog(HRForm.this, "Employee deleted successfully: " + data, "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the table
                    fetchDataFromAPI(API_URL);
                } else {
                    // Show error message
                    JOptionPane.showMessageDialog(HRForm.this, "Error deleting employee: " + data, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(HRForm.this, "Error deleting employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Add action listener
    private class AddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Open add form and pass HRForm reference
            AddForm addForm = new AddForm(jwtToken, HRForm.this);
            addForm.setVisible(true);
        }
    }

    // Refresh action listener
    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Refresh the table
            fetchDataFromAPI(API_URL);
        }
    }

    // Export Rapport action listener
    private class ExportRapportActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Create the URL for the report API
                String reportUrl = "http://localhost:8080/api/v1/report/rapport";

                // Send a GET request to the report API
                HttpURLConnection connection = (HttpURLConnection) new URL(reportUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + jwtToken); // Add JWT token to header

                // Check the response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Set up the file save dialog
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save Report");
                    fileChooser.setSelectedFile(new File("employee_report.csv")); // Default file name

                    int userSelection = fileChooser.showSaveDialog(HRForm.this);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();

                        // Write the response to the selected file
                        try (InputStream inputStream = connection.getInputStream();
                             FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        JOptionPane.showMessageDialog(HRForm.this, "Report exported successfully to: " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Handle error response
                    Scanner scanner = new Scanner(connection.getErrorStream());
                    StringBuilder errorResponse = new StringBuilder();
                    while (scanner.hasNext()) {
                        errorResponse.append(scanner.nextLine());
                    }
                    scanner.close();
                    JOptionPane.showMessageDialog(HRForm.this, "Error exporting report: " + errorResponse.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(HRForm.this, "Error exporting report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Send authenticated DELETE request to API
    private String sendAuthenticatedDeleteRequest(String url) throws IOException {
        System.out.println("Sending DELETE request to: " + url); // Log the request URL
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken); // Add JWT token to header

        int responseCode = connection.getResponseCode();
        System.out.println("DELETE Response Code: " + responseCode); // Log the response code

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

    // Method to refresh the table
    public void refreshTable() {
        fetchDataFromAPI(API_URL); // Refresh the table by fetching data from the API
    }

    public static void main(String[] args) {
        // For testing purposes, you can pass a dummy JWT token
        SwingUtilities.invokeLater(() -> new HRForm("dummy-jwt-token"));
    }
}