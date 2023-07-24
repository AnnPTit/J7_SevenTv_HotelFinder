package com.example.demo.service;


import com.example.demo.entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComboService {
    Page<Combo> getAll(Pageable pageable);

    Page<Combo> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, Pageable pageable);

    Combo findById(String id);

    Combo add(Combo combo);

    boolean existsByCode(String code);
}
