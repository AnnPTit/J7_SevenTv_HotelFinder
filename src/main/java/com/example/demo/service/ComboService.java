package com.example.demo.service;


import com.example.demo.entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ComboService {
    Page<Combo> getAll(Pageable pageable);

    Page<Combo> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, Pageable pageable);

    List<Combo> getAll();

    Combo findById(String id);

    Combo add(Combo combo);

    boolean existsByCode(String code);

    List<Combo> searchCombosWithService(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end, int pageSize , int offset);
    long countSearch(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end);


}
