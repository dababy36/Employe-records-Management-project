package com.employe_management.erms.controller;


import com.employe_management.erms.DTOs.AuthRequest;
import com.employe_management.erms.DTOs.AuthResponse;
import com.employe_management.erms.DTOs.RegisterRequest;
import com.employe_management.erms.reponse.ApiReponse;
import com.employe_management.erms.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/authenticate")
    public ResponseEntity<ApiReponse> authenticate(@RequestBody AuthRequest request) {
       try {
          Object object= service.authenticate(request);
            return ResponseEntity.ok(new ApiReponse("success",  object));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiReponse("error", e.getMessage()));

       }

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }
}
