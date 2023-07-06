package com.example.demo.service;

import com.example.demo.entity.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FloorService {

    Page<Floor> getAll(Pageable pageable);

    Floor getFloorById(String id);

    Floor add(Floor floor);

    void delete(String id);

}
