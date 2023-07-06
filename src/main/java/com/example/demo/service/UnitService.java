package com.example.demo.service;

import com.example.demo.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnitService {

    Page<Unit> getAll(Pageable pageable);

    Unit save(Unit unit);

    boolean delete(String id);

    Unit getById(String id);
}
