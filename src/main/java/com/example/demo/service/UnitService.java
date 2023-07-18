package com.example.demo.service;

import com.example.demo.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {

    Page<Unit> getAll(Pageable pageable);

    List<Unit> findAll();

    Unit save(Unit unit);

    boolean delete(String id);

    Unit getById(String id);
}
