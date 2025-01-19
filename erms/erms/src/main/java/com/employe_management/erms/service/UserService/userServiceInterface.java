package com.employe_management.erms.service.UserService;

import com.employe_management.erms.DTOs.AuthResponse;
import com.employe_management.erms.DTOs.RegisterRequest;
import com.employe_management.erms.DTOs.UserDTOs;
import com.employe_management.erms.entity.user;

import java.util.List;
import java.util.Optional;

public interface userServiceInterface {

     List<user> getAllUsers();


     user getUserById(Long Id);
     void deleteUserById(Long Id);
     user updateUser(Long Id,String role);
     AuthResponse createUser(RegisterRequest UserDTOs);
}
