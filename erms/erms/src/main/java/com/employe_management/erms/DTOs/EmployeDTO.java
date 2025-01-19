package com.employe_management.erms.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Data
@Getter
@Setter
public class EmployeDTO {
    private Long id;
    private String fullName;
    private String email;
    private String department;
    private String address;
    private String phoneNumber;
    private LocalDate hireDate;
    private String jobTitle;
    private String employementStatus;
}
