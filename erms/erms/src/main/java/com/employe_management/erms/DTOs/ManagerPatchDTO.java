package com.employe_management.erms.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ManagerPatchDTO {

    private String employementStatus;

    private String jobTitle;
}
