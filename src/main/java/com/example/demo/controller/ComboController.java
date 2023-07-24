package com.example.demo.controller;

import com.example.demo.entity.Combo;
import com.example.demo.service.ComboService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/combo")
public class ComboController {
    @Autowired
    private ComboService comboService;

    @GetMapping("/load")
    public Page<Combo> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return comboService.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Combo> detail(@PathVariable("id") String id) {
        Combo combo = comboService.findById(id);
        return new ResponseEntity<Combo>(combo, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Combo> save(@Valid
                                      @RequestBody Combo combo,
                                      BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (comboService.existsByCode(combo.getComboCode())) {
            return new ResponseEntity("Combo Code is exists !", HttpStatus.BAD_REQUEST);
        }
        combo.setCreateAt(new Date());
        combo.setUpdateAt(new Date());
        combo.setStatus(1);
        comboService.add(combo);
        return new ResponseEntity<Combo>(combo, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Combo> update(
            @PathVariable("id") String id,
            @Valid
            @RequestBody Combo combo,
            BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        combo.setId(id);
        combo.setUpdateAt(new Date());
        comboService.add(combo);
        return new ResponseEntity<Combo>(combo, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Combo> detele(@PathVariable("id") String id) {
        Combo combo = comboService.findById(id);
        combo.setStatus(0);
        comboService.add(combo);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

}
