package com.employe_management.erms.DTOs;

import com.employe_management.erms.enums.role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private role role;
    private String department;
}
