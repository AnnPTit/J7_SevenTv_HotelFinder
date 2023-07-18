package com.example.demo.service.impl;

import com.example.demo.entity.Floor;
import com.example.demo.repository.FloorRepository;
import com.example.demo.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Override
    public List<Floor> getList() {
        return floorRepository.findAll();
    }

    @Override
    public Page<Floor> getAll(Pageable pageable) {
        return floorRepository.findAll(pageable);
    }

    @Override
    public Page<Floor> findByCodeOrName(String key, Pageable pageable) {
        return floorRepository.findByCodeOrName(key , "%" +key +"%" , pageable);
    }

    @Override
    public Floor getFloorById(String id) {
        return floorRepository.findById(id).orElse(null);
    }

    @Override
    public Floor add(Floor floor) {
        try {
            return floorRepository.save(floor);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            floorRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return floorRepository.existsByFloorCode(code);
    }
}
