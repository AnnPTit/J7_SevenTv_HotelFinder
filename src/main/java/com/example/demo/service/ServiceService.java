package com.example.demo.service;


import com.example.demo.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {
    Page<Service> getAll(Pageable pageable);

    Service findById(String id);

    Service add(Service serviceType);

    boolean existsByCode(String code);

}
