package com.employe_management.erms.controller;

import com.employe_management.erms.entity.departement;
import com.employe_management.erms.reponse.ApiReponse;
import com.employe_management.erms.service.DepartementService.DepartementServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/departement")
public class DepartementController {


    private final DepartementServiceInterface departementService;
    @GetMapping("/all")
    public ResponseEntity<ApiReponse> getAllDepartements(){
        List<departement> departements= departementService.getAllDepartement();
        return ResponseEntity.ok(new ApiReponse("success",departements));
    }


}
