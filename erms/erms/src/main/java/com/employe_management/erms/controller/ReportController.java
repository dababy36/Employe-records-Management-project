package com.employe_management.erms.controller;

import com.employe_management.erms.service.ReportingService.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {


    private final ReportService reportService;
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @GetMapping("/rapport")
    public void generateEmployeeReport(HttpServletResponse response) throws IOException {
        reportService.generateEmployeeReport(response);  // Call the service to generate the report
    }}
