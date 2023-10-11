package com.example.demo.service.impl;

import com.example.demo.entity.Combo;
import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.OrderDetail;
import com.example.demo.repository.ComboUsedRepository;
import com.example.demo.service.ComboUsedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboUsedServiceImpl implements ComboUsedService {

    @Autowired
    private ComboUsedRepository comboUsedRepository;

    @Override
    public List<ComboUsed> getAll() {
        return comboUsedRepository.findAll();
    }

    @Override
    public List<ComboUsed> getAllByOrderDetailId(String id) {
        return comboUsedRepository.getAllByOrderDetailId(id);
    }

    @Override
    public ComboUsed getById(String id) {
        return comboUsedRepository.findById(id).orElse(null);
    }

    @Override
    public ComboUsed add(ComboUsed comboUsed) {
        return comboUsedRepository.save(comboUsed);
    }

    @Override
    public void delete(ComboUsed comboUsed) {
        comboUsedRepository.delete(comboUsed);
    }

    @Override
    public ComboUsed getByCombo(String combo, String orderDetail) {
        return comboUsedRepository.getByCombo(combo, orderDetail);
    }

    @Override
    public void updateQuantityComboUsed(Integer quantity, String comboId) {
        comboUsedRepository.updateQuantityComboUsed(quantity, comboId);
    }
}
