package com.employe_management.erms.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTOs {
    private String name;
    private String email;
    private String department;
    private String role;
    private String password;


}
