package com.example.demo.service;

import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceTypeService {

    Page<ServiceType> getAll(Pageable pageable);

    List<ServiceType> findAll();

    Page<ServiceType> findByCodeOrName(String key, Pageable pageable);

    ServiceType findById(String id);

    ServiceType add(ServiceType serviceType);

    boolean existsByCode(String code);
}
