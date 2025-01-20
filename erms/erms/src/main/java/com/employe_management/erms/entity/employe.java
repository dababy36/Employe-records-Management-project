package com.employe_management.erms.entity;

import com.employe_management.erms.configs.EmployeeAuditListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.employe_management.erms.enums.employementStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor

@Table(name = "employe")
@EntityListeners(EmployeeAuditListener.class)
public class employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be in the format xxx@xxx.xx")
    private String email;
    @Column(nullable = false)
    private String jobTitle;


    @Column(nullable = false)
    private employementStatus employementStatus;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDate hireDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
     // Prevent infinite recursion when call to JSON
    private departement departement;


    public employe(String johnDoe, String mail, String softwareEngineer, com.employe_management.erms.enums.employementStatus employementStatus, String s, String s1, LocalDate of, com.employe_management.erms.entity.departement hrDept) {
    }
}
