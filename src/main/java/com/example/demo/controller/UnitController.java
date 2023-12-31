package com.example.demo.controller;

import com.example.demo.entity.Unit;
import com.example.demo.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @GetMapping("/load")
    public Page<Unit> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<Unit> page = unitService.getAll(pageable);
        return page;
    }

    @GetMapping("/getAll")
    public List<Unit> findAll() {
        return unitService.findAll();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") String id) {
        Unit unit = unitService.getById(id);
        if (unit == null) {
            String errorMessage = "Không tìm thấy đối tượng với id: " + id;
            return ResponseEntity.badRequest().body(errorMessage);
        }
        return ResponseEntity.ok(unit);
    }

    @PostMapping("/save")
    public ResponseEntity<Unit> save(@RequestBody Unit unit) {
        unit.setCreateAt(new Date());
        unit.setUpdateAt(new Date());
        unit.setStatus(1);
        unitService.save(unit);
        return ResponseEntity.ok(unit);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Unit> update(@PathVariable("id") String id, @RequestBody Unit unit) {
        unit.setId(id);
        unit.setUpdateAt(new Date());
        unit.setStatus(1);
        unitService.save(unit);
        return ResponseEntity.ok(unit);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        Unit unit = unitService.getById(id);
        unit.setStatus(0);
        unitService.save(unit);
        return ResponseEntity.ok().build();
    }
}
