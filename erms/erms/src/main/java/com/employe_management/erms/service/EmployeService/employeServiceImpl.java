package com.employe_management.erms.service.EmployeService;

import com.employe_management.erms.DTOs.EmployeDTO;
import com.employe_management.erms.entity.departement;
import com.employe_management.erms.entity.employe;
import com.employe_management.erms.enums.employementStatus;
import com.employe_management.erms.exceptions.AlreadyExistResouceException;
import com.employe_management.erms.exceptions.EmployeNotFoundException;
import com.employe_management.erms.exceptions.ResourceNotFoundException;
import com.employe_management.erms.repository.employeeRepository;
import com.employe_management.erms.repository.departementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class employeServiceImpl implements employeInterface {

    final private employeeRepository employeRepository;
    final private departementRepository departementRepository;
    final private ModelMapper  modelMapper;


    @Override
    public employe addEmploye(EmployeDTO employe) {
        // Find the department by the name of the department for the new employee
        departement newDepartement = departementRepository.findByName(employe.getDepartment());

        // Check if the department exists
        if (newDepartement == null) {
            throw new ResourceNotFoundException("Department with name " + employe.getDepartment() + " not found");
        }

        // Check if the employee with the email already exists
        if (employeRepository.existsByEmail(employe.getEmail())) {
            throw new AlreadyExistResouceException("Employee with email " + employe.getEmail() + " already exists");
        }

        // Create a new employee and set properties
        employe newEmploye = new employe();
        newEmploye.setFullName(employe.getFullName());
        newEmploye.setEmail(employe.getEmail());
        newEmploye.setDepartement(newDepartement);
        newEmploye.setAddress(employe.getAddress());
        newEmploye.setPhoneNumber(employe.getPhoneNumber());
        newEmploye.setHireDate(employe.getHireDate());
        newEmploye.setJobTitle(employe.getJobTitle());
        newEmploye.setEmployementStatus(employementStatus.ACTIVE);  // Assuming you're setting it to ACTIVE by default

        // Save the new employee
        return employeRepository.save(newEmploye);
    }

    @Override
    public employe updateEmploye(Long id,EmployeDTO employe) {
        //check if the employee exists
        employe existingEmploye = employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException("Employee with ID " + id + " not found"));
        //check if the department exists
        if (employe.getDepartment() != null) {
            // Find the department by name
            departement updatedDepartement = departementRepository.findByName(employe.getDepartment());

            // If the department is not found, throw ResourceNotFoundException
            if (updatedDepartement == null) {
                throw new ResourceNotFoundException("Department with name " + employe.getDepartment() + " not found");
            }

            // Set the department to the existing employee
            existingEmploye.setDepartement(updatedDepartement);
        }
        //check if the employee with the email already exists and if exists throw an exception
        if (employeRepository.existsByEmail(employe.getEmail())) {
            Optional<employe> existingEmployeByEmail = employeRepository.findByEmail(employe.getEmail());
            if (existingEmployeByEmail.isPresent() && !existingEmployeByEmail.get().getId().equals(id)) {
                throw new AlreadyExistResouceException("Employee with email " + employe.getEmail() + " already exists");
            }
        }




        //now map the new values to the existing employee
        modelMapper.map(employe, existingEmploye);
        return employeRepository.save(existingEmploye);

    }

    // Get the employee by ID
    @Override
    public employe getEmployeById(Long id) {
        //check if the employee exists if not throw an exception
        return employeRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException("Employee with ID " + id + " not found"));
    }

    @Override
    public void deleteEmploye(Long id) {
        //check if the employee exists if not throw an exception  and then delete it
        employeRepository.findById(id).ifPresentOrElse(employeRepository::delete, () -> {
            throw new EmployeNotFoundException("Employee with ID " + id + " not found");
        });


    }

    @Override
    public List<employe> getAllEmploye() {
        //return all employees
        return employeRepository.findAll();
    }

    @Override
    public List<employe> findByDepartment(String department) {
        //check if the department exists if not throw an exception

     if(!departementRepository.existsByName(department)) {
         throw new ResourceNotFoundException("Department with name " + department + " not found");
     }
       //return the list of employees in the department

        departement NewDepartement = departementRepository.findByName(department);

        return employeRepository.findByDepartement(NewDepartement);
    }

    @Override
    public List<employe> findByJobTitle(String jobTitle) {
        return employeRepository.findByJobTitle(jobTitle);
    }

    @Override
    public List<employe> filterEmployees(employementStatus employementStatus, String department, LocalDate startDate, LocalDate endDate) {
        //check if the department exists if not throw an exception
        if(!departementRepository.existsByName(department)) {
            throw new ResourceNotFoundException("Department with name " + department + " not found");
        }
        //return the list of employees in the department
        departement NewDepartement = departementRepository.findByName(department);

        return employeRepository.findByEmployementStatusAndDepartementAndHireDateBetween(employementStatus, NewDepartement, startDate, endDate);
    }


}
