package com.example.demo.service;

import com.example.demo.entity.Position;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    List<Position> getAll();

    Position getPositionById(String id);

    Position add(Position position);

    Boolean delete(String id);
}
