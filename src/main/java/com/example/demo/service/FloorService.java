package com.example.demo.service;

import com.example.demo.entity.Floor;

import java.util.List;

public interface FloorService {

    List<Floor> getAll();

    Floor getFloorById(String id);

    Floor add(Floor floor);

    void delete(String id);

}
