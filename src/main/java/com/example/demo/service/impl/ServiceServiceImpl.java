package com.example.demo.service.impl;

import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public Page<com.example.demo.entity.Service> load(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }


    @Override
    public Page<com.example.demo.entity.Service> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, Pageable pageable) {
        return serviceRepository.loadAndSearch(
                (serviceCode != null && !serviceCode.isEmpty()) ? serviceCode : null,
                (serviceName != null && !serviceName.isEmpty()) ? "%" + serviceName + "%" : null,
                (serviceTypeId != null && !serviceTypeId.isEmpty()) ? serviceTypeId : null,
                (unitId != null && !unitId.isEmpty()) ? unitId : null,
                pageable
        );
    }

    @Override
    public List<com.example.demo.entity.Service> getAll() {
        return serviceRepository.getAll();
    }

    @Override
    public com.example.demo.entity.Service findById(String id) {
        return serviceRepository.findById(id).orElse(null);
    }

    @Override
    public com.example.demo.entity.Service add(com.example.demo.entity.Service service) {
        try {
            serviceRepository.save(service);
            return service;

        } catch (Exception e) {
            System.err.println("Error in save service");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return serviceRepository.existsByServiceCode(code);
    }
}
