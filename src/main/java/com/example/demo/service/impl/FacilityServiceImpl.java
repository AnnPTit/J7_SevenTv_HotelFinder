package com.example.demo.service.impl;

import com.example.demo.entity.Facility;
import com.example.demo.repository.FacilityRepository;
import com.example.demo.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }

    @Override
    public Facility findById(String id) {
        return facilityRepository.findById(id).orElse(null);
    }

}
