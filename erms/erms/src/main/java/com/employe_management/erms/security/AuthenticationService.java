package com.employe_management.erms.security;

import com.employe_management.erms.DTOs.AuthRequest;
import com.employe_management.erms.DTOs.AuthResponse;

import com.employe_management.erms.DTOs.RegisterRequest;
import com.employe_management.erms.entity.departement;
import com.employe_management.erms.exceptions.AlreadyExistResouceException;
import com.employe_management.erms.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.employe_management.erms.entity.user;
import com.employe_management.erms.repository.departementRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final userRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final departementRepository departmentRepository;

    //create new user and save it to the database

    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistResouceException("Email already exists");
        }
        //check if daprtement of new user exists else throw exception

        departement RegisterDepartment= departmentRepository.findByName(request.getDepartment());
        if(RegisterDepartment==null){
            throw new AlreadyExistResouceException("Department does not exist");
        }

        // Create new user
        var NewUser = user.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .departement(RegisterDepartment)
                .active(true)
                .build();

        // Save user
        repository.save(NewUser);


        //return jwt
        var jwtToken = jwtService.generateToken(NewUser);
        return new AuthResponse(jwtToken,"","");
    }

    // login function with credentiels(email,password)
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken,user.getRole().name(),user.getDepartement().getName());
    }
}