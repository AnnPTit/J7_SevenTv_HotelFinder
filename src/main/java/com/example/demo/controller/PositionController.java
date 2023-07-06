package com.example.demo.controller;

import com.example.demo.entity.Deposit;
import com.example.demo.entity.Position;
import com.example.demo.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/position")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/load")
    public List<Position> getAll() {
        return positionService.getAll();
    }

    @PostMapping("/add")
    public Position add(@RequestBody Position position) {
        position.setCreateAt(new Date());
        return positionService.add(position);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable("id") String id) {
        return positionService.delete(id);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Position> detail(@PathVariable("id") String id) {
        return ResponseEntity.ok(positionService.getPositionById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Position> update(@PathVariable("id") String id, @RequestBody Position position) {
        position.setId(id);
        position.setUpdateAt(new Date());
        positionService.add(position);
        return ResponseEntity.ok(position);
    }
}
