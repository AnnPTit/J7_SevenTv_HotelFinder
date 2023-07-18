package com.example.demo.service.impl;

import com.example.demo.entity.Unit;
import com.example.demo.repository.UnitRepository;
import com.example.demo.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Override
    public Page<Unit> getAll(Pageable pageable) {
        return unitRepository.findAll(pageable);
    }

    @Override
    public List<Unit> findAll() {
        return unitRepository.getAll();
    }

    @Override
    public Unit save(Unit unit) {
        try {
            unitRepository.save(unit);
            return unit;
        } catch (Exception e) {
            System.err.println("Error in save Unit ");
            return null;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            unitRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error in delete Unit ");
            return false;
        }
    }

    @Override
    public Unit getById(String id) {
        return unitRepository.findById(id).orElse(null);
    }
}
