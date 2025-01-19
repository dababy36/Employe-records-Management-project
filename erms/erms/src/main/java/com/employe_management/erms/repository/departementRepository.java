package com.employe_management.erms.repository;

import com.employe_management.erms.entity.departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface departementRepository extends JpaRepository<departement,Long> {
    departement findByName(String department);
    Boolean existsByName(String department);
}
