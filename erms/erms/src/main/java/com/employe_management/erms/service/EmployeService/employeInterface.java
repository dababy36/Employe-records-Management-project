package com.employe_management.erms.service.EmployeService;

import com.employe_management.erms.DTOs.EmployeDTO;
import com.employe_management.erms.entity.departement;
import com.employe_management.erms.entity.employe;
import com.employe_management.erms.enums.employementStatus;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface employeInterface {
    //define the methods that will be implemented in the service class


    employe addEmploye(EmployeDTO employe);

    employe updateEmploye(Long id,EmployeDTO employe);

    employe getEmployeById(Long id);

    void deleteEmploye(Long id);

    List<employe> getAllEmploye();

    List<employe> findByDepartment(String department);
    List<employe> findByJobTitle(String jobTitle);

    List<employe> filterEmployees(employementStatus employementStatus, String department, LocalDate startDate, LocalDate endDate);
}
