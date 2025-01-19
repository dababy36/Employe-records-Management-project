package com.employe_management.erms.service.DepartementService;
import com.employe_management.erms.entity.departement;
import com.employe_management.erms.exceptions.ResourceNotFoundException;
import com.employe_management.erms.repository.departementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service


public class DepartementServiceImp implements DepartementServiceInterface {

    private final  departementRepository departementRepository;

    @Autowired
    public DepartementServiceImp(departementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    @Override
    public List<departement> getAllDepartement() {

        return departementRepository.findAll();

    }}

