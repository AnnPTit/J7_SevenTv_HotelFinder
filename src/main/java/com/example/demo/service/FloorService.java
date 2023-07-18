package com.example.demo.service;

import com.example.demo.entity.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FloorService {

    List<Floor> getList();

    Page<Floor> getAll(Pageable pageable);

    Page<Floor> findByCodeOrName(String key , Pageable pageable);

    Floor getFloorById(String id);

    Floor add(Floor floor);

    void delete(String id);

    boolean existsByCode(String code);

}
