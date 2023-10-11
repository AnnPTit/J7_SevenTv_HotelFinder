package com.example.demo.service;

import com.example.demo.entity.Combo;
import com.example.demo.entity.ComboUsed;
import com.example.demo.entity.OrderDetail;

import java.util.List;

public interface ComboUsedService {

    List<ComboUsed> getAll();

    List<ComboUsed> getAllByOrderDetailId(String id);

    ComboUsed getById(String id);

    ComboUsed add(ComboUsed comboUsed);

    void delete(ComboUsed comboUsed);

    ComboUsed getByCombo(String combo, String orderDetail);

    void updateQuantityComboUsed(Integer quantity, String comboId);

}
