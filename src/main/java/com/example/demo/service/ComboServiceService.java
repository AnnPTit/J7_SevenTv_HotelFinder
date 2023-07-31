package com.example.demo.service;


import com.example.demo.entity.ComboService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComboServiceService {
    Page<ComboService> getAll(Pageable pageable);

//    Page<Combo> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, Pageable pageable);

    ComboService findById(String id);

    ComboService add(ComboService comboService);

    ComboService findByComboAndService(String comboID, String serviceID);

    boolean deteleComboService(String comboId, String serviceID);

}
