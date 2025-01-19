package com.employe_management.erms.exceptions;

public class EmployeNotFoundException extends RuntimeException {
    public EmployeNotFoundException(String message) {
        super(message);
    }
}
