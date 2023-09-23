package com.example.demo.service;

import com.example.demo.entity.ComboUsed;

import java.util.List;

public interface ComboUsedService {

    List<ComboUsed> getAll();

    List<ComboUsed> getAllByOrderDetailId(String id);

    ComboUsed getById(String id);

    ComboUsed add(ComboUsed comboUsed);

    void delete(ComboUsed comboUsed);

}
