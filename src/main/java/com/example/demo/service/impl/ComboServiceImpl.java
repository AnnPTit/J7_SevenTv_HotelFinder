package com.example.demo.service.impl;

import com.example.demo.entity.Combo;
import com.example.demo.repository.ComboRepository;
import com.example.demo.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ComboServiceImpl implements ComboService {

    @Autowired
    private ComboRepository comboRepository;


    @Override
    public Page<Combo> getAll(Pageable pageable) {
        return comboRepository.findAll(pageable);
    }

    @Override
    public Page<Combo> loadAndSearch(String serviceCode, String serviceName, String serviceTypeId, String unitId, Pageable pageable) {
        return null;
    }

    @Override
    public Combo findById(String id) {
        return comboRepository.findById(id).orElse(null);
    }

    @Override
    public Combo add(Combo combo) {
        try {
            return comboRepository.save(combo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return comboRepository.existsByComboCode(code);
    }

    @Override
    public List<Combo> searchCombosWithService(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end, int pageSize, int offset) {
        return comboRepository.searchCombosWithService(
                (comboCode != null && !comboCode.isEmpty()) ? comboCode : null,
                (comboName != null && !comboName.isEmpty()) ? "%" + comboName + "%" : null,
                (serviceId != null && !serviceId.isEmpty()) ? serviceId : null,
                start,
                end,
                pageSize, offset
        );
    }

    @Override
    public long countSearch(String comboCode, String comboName, String serviceId, BigDecimal start, BigDecimal end) {
        return comboRepository.countSearch(
                (comboCode != null && !comboCode.isEmpty()) ? comboCode : null,
                (comboName != null && !comboName.isEmpty()) ? "%" + comboName + "%" : null,
                (serviceId != null && !serviceId.isEmpty()) ? serviceId : null,
                start,
                end);
    }


}
