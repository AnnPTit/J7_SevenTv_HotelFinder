package com.example.demo.service.impl;

import com.example.demo.entity.ComboService;
import com.example.demo.repository.ComboServiceRepository;
import com.example.demo.service.ComboServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ComboServiceServiceImpl implements ComboServiceService {

    @Autowired
    private ComboServiceRepository comboServiceRepository;

    @Override
    public Page<ComboService> getAll(Pageable pageable) {
        return comboServiceRepository.findAll(pageable);
    }

    @Override
    public ComboService findById(String id) {
        return comboServiceRepository.findById(id).orElse(null);
    }

    @Override
    public ComboService add(ComboService comboService) {
        try {
            return comboServiceRepository.save(comboService);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ComboService findByComboAndService(String comboID, String serviceID) {
        return comboServiceRepository.findByComboAndService(comboID, serviceID);
    }

    @Override
    public boolean deteleComboService(String comboId, String serviceID) {
        try {
            comboServiceRepository.deteleComboService(comboId, serviceID);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
