package com.employe_management.erms.controller;

import com.employe_management.erms.DTOs.RegisterRequest;
import com.employe_management.erms.DTOs.UserDTOs;
import com.employe_management.erms.entity.user;
import com.employe_management.erms.exceptions.ResourceNotFoundException;
import com.employe_management.erms.reponse.ApiReponse;
import com.employe_management.erms.service.UserService.userServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final userServiceInterface userService;

    //Endpoint to get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ADMIN/users/all")
    public ResponseEntity<ApiReponse> getAllUsers() {
        return ResponseEntity.ok(new ApiReponse("success", userService.getAllUsers()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ADMIN/users/{userId}/delete")
    public ResponseEntity<ApiReponse> deleteUserById(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok(new ApiReponse("success", "User deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
    }}
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ADMIN/user/{userId}")
    public ResponseEntity<ApiReponse> getUserById(@PathVariable  Long userId) {
        try {
            return ResponseEntity.ok(new ApiReponse("success", userService.getUserById(userId)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/ADMIN/user/{userId}/update")
    public ResponseEntity<ApiReponse> updateUser(@PathVariable Long userId,@RequestBody UserDTOs role) {
        try {
            userService.updateUser(userId, role.getRole());
            return ResponseEntity.ok(new ApiReponse("success", "User updated successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ADMIN/user/create")
    public ResponseEntity<ApiReponse> createUser(@RequestBody  RegisterRequest UserDTOs) {
        try{
        userService.createUser(UserDTOs);
        return ResponseEntity.ok(new ApiReponse("success",  "User created successfully"));
    }catch (Exception e){
        return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));
    }
    }

}