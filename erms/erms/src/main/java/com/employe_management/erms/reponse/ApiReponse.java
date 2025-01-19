package com.employe_management.erms.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiReponse {
    private String message;

    private Object data;
}
