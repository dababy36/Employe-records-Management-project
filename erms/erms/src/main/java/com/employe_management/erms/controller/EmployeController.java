package com.employe_management.erms.controller;

import com.employe_management.erms.DTOs.EmployeDTO;
import com.employe_management.erms.DTOs.ManagerPatchDTO;
import com.employe_management.erms.entity.departement;
import com.employe_management.erms.entity.employe;
import com.employe_management.erms.entity.user;
import com.employe_management.erms.enums.employementStatus;
import com.employe_management.erms.exceptions.AlreadyExistResouceException;
import com.employe_management.erms.exceptions.ResourceNotFoundException;
import com.employe_management.erms.reponse.ApiReponse;
import com.employe_management.erms.security.JwtService;
import com.employe_management.erms.service.EmployeService.employeServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.employe_management.erms.repository.userRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor

@RestController
@RequestMapping("api/v1/employee")
@CrossOrigin(origins = "*")
public class EmployeController {


    private final employeServiceImpl employeService;
    private final JwtService    jwtService;
    private final userRepository userRepository;


    //HR and ADMIN Apis Endpoints


    //Get all employes
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @GetMapping("/HR/all")
    public ResponseEntity<ApiReponse> getAllEmployes(){
        //get all employes
        List<employe> employes= employeService.getAllEmploye();
        return ResponseEntity.ok(new ApiReponse("success",employes));
    }
    //Add new employe
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @PostMapping("/HR/add")
    public ResponseEntity<ApiReponse> addEmploye(@RequestBody  EmployeDTO NewEmploye){
       try{
                //try to add new employe
              employe employe= employeService.addEmploye(NewEmploye);
                //return success response
              return ResponseEntity.ok(new ApiReponse("success",employe));
       }catch(AlreadyExistResouceException e){
                //catch exception if employe already exist
              return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }catch (ResourceNotFoundException e){
           //catch exception if department not found
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }catch (Exception e){
            //catch exception if any other error
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }
    }


    //Update employe
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @PutMapping("/HR/{userId}/update")

    public ResponseEntity<ApiReponse> updateEmploye(@PathVariable("userId") Long userId, @RequestBody EmployeDTO employeDTO){
        try{
            //try to update employe
        employe employe= employeService.updateEmploye(userId,employeDTO);
            //return success response if employe updated successfully
        return ResponseEntity.ok(new ApiReponse("success",employe));
    } catch(ResourceNotFoundException e){
        //catch exception if employe not found
        return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
    }catch (Exception e){
        //catch exception if any other error
        return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
    }}

    //delete employe by id
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @DeleteMapping("/HR/{userId}/delete")

    public ResponseEntity<ApiReponse> deleteEmploye(@PathVariable("userId") Long userId){

        try{
            //try to delete employe
            employeService.deleteEmploye(userId);
            //return success response if employe deleted successfully
            return ResponseEntity.ok(new ApiReponse("success","Employe deleted successfully"));
        }catch(ResourceNotFoundException e){
            //catch exception if employe not found
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }catch (Exception e){
            //catch exception if any other error
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }
    }

    //filter employes by status and department and Hiredate
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @GetMapping("/filterUsers")
    public ResponseEntity<ApiReponse> filterUsers(@RequestParam employementStatus employementStatus, @RequestParam String department, @RequestParam String startDate, @RequestParam String endDate) {
        // Convert the date strings to LocalDate
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Filter the employees
        try{
            //try to filter employes
            List<employe> employees = employeService.filterEmployees(employementStatus, department, start, end);
            //return success message
        return ResponseEntity.ok(new ApiReponse("success", employees));

        }catch(ResourceNotFoundException e){
            //catch department dosen't exist error and return error msg
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }catch (Exception e)
        {
            //catch other exeption
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }

    }


    //get employee data by Id
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') ")
    @GetMapping("/{userId}/getEmploye")

    public ResponseEntity<ApiReponse> getEmployeById(@PathVariable("userId") Long userId){
        try{
            employe employe= employeService.getEmployeById(userId);
            return ResponseEntity.ok(new ApiReponse("success",employe));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.ok(new ApiReponse("error",e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    @GetMapping("/HR/employe/jobTitle")
    public ResponseEntity<ApiReponse> getEmployeByJobTitle(@RequestParam String jobTitle){
        try{
            List<employe> employes= employeService.findByJobTitle(jobTitle);
            return ResponseEntity.ok(new ApiReponse("success",employes));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(new ApiReponse("error",e.getMessage()));
        }
    }










    //Manager APIs Endpoint
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') ")

    @GetMapping("/Manager/employe/by-department")
    public ResponseEntity<List<employe>> getEmployeesByDepartment(@RequestHeader("Authorization") String token) {
        // Extract all claims from the JWT
        String NewToken = token.substring(7).trim();
        Claims claims = jwtService.extractAllClaims(NewToken);

        // Extract the email from the claims
        String email = claims.getSubject(); // Assuming the email is stored as the subject

        // Get the user's department based on the email
        Optional<user> user = userRepository.findByEmail(email);
        String departmentName = user.get().getDepartement().getName();

        if (departmentName == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Unauthorized if no department found
        }

        // Get employees in the department
        List<employe> employees = employeService.findByDepartment(departmentName);
        return ResponseEntity.ok(employees);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/Manager/{userId}/update")
    public ResponseEntity<ApiReponse> updateEmployeByManager(@PathVariable("userId") Long userId, @RequestBody ManagerPatchDTO employeDTO){
        try{
            //create new employeDTO object
            EmployeDTO employeDTO1=new EmployeDTO();
            employeDTO1.setJobTitle(employeDTO.getJobTitle());
            employeDTO1.setEmployementStatus(employeDTO.getEmployementStatus());
            employe employe= employeService.updateEmploye(userId,employeDTO1);
            return ResponseEntity.ok(new ApiReponse("success",employe));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.ok(new ApiReponse("error",e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/MANAGER/filterUsers")
    public ResponseEntity<ApiReponse> filterUsersByManagers(@RequestHeader("Authorization") String token,@RequestParam employementStatus employementStatus, @RequestParam String startDate, @RequestParam String endDate) {
        // Convert the date strings to LocalDate
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // Extract all claims from the JWT
        String NewToken = token.substring(7).trim();
        Claims claims = jwtService.extractAllClaims(NewToken);

        // Extract the email from the claims
        String email = claims.getSubject(); // Assuming the email is stored as the subject

        // Get the user's department based on the email
        Optional<user> user = userRepository.findByEmail(email);
        String departmentName = user.get().getDepartement().getName();

        // Filter the employees
        try {

            List<employe> employees = employeService.filterEmployees(employementStatus, departmentName, start, end);
            return ResponseEntity.ok(new ApiReponse("success", employees));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.ok(new ApiReponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiReponse("error", e.getMessage()));
        }


    }

    }
