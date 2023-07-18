package com.example.demo.service.impl;

import com.example.demo.entity.ServiceType;
import com.example.demo.repository.ServiceTypeRepository;
import com.example.demo.service.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTypeServiceImpl implements ServiceTypeService {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;


    @Override
    public Page<ServiceType> getAll(Pageable pageable) {
        return serviceTypeRepository.findAll(pageable);
    }

    @Override
    public List<ServiceType> findAll() {
        return serviceTypeRepository.getAll();
    }

    @Override
    public Page<ServiceType> findByCodeOrName(String key, Pageable pageable) {
        return serviceTypeRepository.findByCodeOrName(key , "%" +key +"%" , pageable);
    }

    @Override
    public ServiceType findById(String id) {
        return serviceTypeRepository.findById(id).orElse(null);
    }

    @Override
    public ServiceType add(ServiceType serviceType) {
        try {
            serviceTypeRepository.save(serviceType);
            return serviceType;
        } catch (Exception e) {
            System.err.println("Error in save serviceType");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return serviceTypeRepository.existsByServiceTypeCode(code);
    }
}
