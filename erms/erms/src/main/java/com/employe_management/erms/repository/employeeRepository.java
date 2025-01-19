package com.employe_management.erms.repository;

import com.employe_management.erms.entity.departement;
import com.employe_management.erms.entity.employe;
import com.employe_management.erms.enums.employementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface employeeRepository extends JpaRepository<employe,Long> {
    boolean existsByEmail(String email);

    List<employe> findByDepartement(departement department);

    Optional<employe> findByEmail(String email);

    

    List<employe> findByEmployementStatusAndDepartementAndHireDateBetween(
            employementStatus employeeStatus, departement departement, LocalDate startDate, LocalDate endDate);

    List<employe> findByJobTitle(String jobTitle);
}
