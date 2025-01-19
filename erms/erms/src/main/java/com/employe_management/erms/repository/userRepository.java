package com.employe_management.erms.repository;

import com.employe_management.erms.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<user,Long> {

    Optional<user> findByEmail(String username);
}
