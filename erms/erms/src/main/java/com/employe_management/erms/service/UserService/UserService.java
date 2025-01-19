package com.employe_management.erms.service.UserService;

import com.employe_management.erms.DTOs.AuthResponse;
import com.employe_management.erms.DTOs.RegisterRequest;
import com.employe_management.erms.DTOs.UserDTOs;
import com.employe_management.erms.entity.user;
import com.employe_management.erms.enums.role;
import com.employe_management.erms.exceptions.ResourceNotFoundException;
import com.employe_management.erms.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Service;
import com.employe_management.erms.repository.userRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements userServiceInterface {
    private final userRepository userRepository;
    private final AuthenticationService service;

    @Override
    // This method returns a list of all users
    public List<user> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    // This method returns a user by ID
    public user getUserById(Long Id) {
        // Check if the user exists by finding the user by ID
        return userRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    // This method deletes a user by ID
    public void deleteUserById(Long Id) {
        // Check if the user exists by finding the user by ID
        Optional<user> user = userRepository.findById(Id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        // If the user exists, delete the user
        userRepository.deleteById(Id);


    }

    @Override
    // This method updates a user's role by ID
    public user updateUser(Long Id,String roles) {

        user existingUser = userRepository.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try{
            existingUser.setRole(role.valueOf(roles)); // Set the new role
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roles);
        }



        // Save updated user
        return userRepository.save(existingUser); // Ensure user is saved
    }

    @Override
    // This method creates a new user
    public AuthResponse createUser(RegisterRequest UserDTOs) {
        return service.register(UserDTOs);

    }
}
