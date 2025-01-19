package com.employe_management.erms.service.ReportingService;

import com.employe_management.erms.entity.employe;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.employe_management.erms.repository.employeeRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private employeeRepository employeeRepository;

    public void generateEmployeeReport(HttpServletResponse response) throws IOException {
        List<employe> employees = employeeRepository.findAll();  // Retrieve all employees from the database

        // Set the response headers to indicate a CSV file download
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=employee_report.csv");

        // Create a CSVWriter object to write the CSV data to the response output stream
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            // Write the header row
            writer.writeNext(new String[]{"Employee ID", "Full Name", "Job Title", "Department", "Hire Date", "Employment Status"});

            // Write each employee's data
            for (employe employee : employees) {
                writer.writeNext(new String[]{
                        String.valueOf(employee.getId()),
                        employee.getFullName(),
                        employee.getJobTitle(),
                        employee.getDepartement().getName(),
                        employee.getHireDate().toString(),
                        employee.getEmployementStatus().toString()
                });
            }
        }
    }
}
