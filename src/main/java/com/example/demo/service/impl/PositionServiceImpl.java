package com.example.demo.service.impl;

import com.example.demo.entity.Position;
import com.example.demo.repository.PositionRepository;
import com.example.demo.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    @Override
    public Position getPositionById(String id) {
        return positionRepository.findById(id).orElse(null);
    }

    @Override
    public Position add(Position position) {
        if (position != null) {
            return positionRepository.save(position);
        }
        return null;
    }


    @Override
    public Boolean delete(String id) {
        try {
            positionRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
