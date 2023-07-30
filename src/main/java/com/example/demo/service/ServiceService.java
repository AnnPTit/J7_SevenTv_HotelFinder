package com.example.demo.service;


import com.example.demo.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceService {
    Page<Service> load(Pageable pageable);

    Page<Service> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, BigDecimal start, BigDecimal end, Pageable pageable);

    List<Service> getAll();

    Service findById(String id);

    Service add(Service serviceType);

    boolean existsByCode(String code);

}
