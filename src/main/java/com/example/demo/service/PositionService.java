package com.example.demo.service;

import com.example.demo.entity.Position;

import java.util.List;

public interface PositionService {

    List<Position> getAll();

    Position getPositionById(String id);

    Position add(Position position);

    Boolean delete(String id);

    Position getIdPosition();
}
