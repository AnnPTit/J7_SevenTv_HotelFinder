package com.example.demo.controller;

import com.example.demo.entity.ComboService;
import com.example.demo.service.ComboServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/combo-service")
public class ComboServiceController {
    @Autowired
    private ComboServiceService comboServiceService;

    @GetMapping("/load")
    public Page<ComboService> load(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return comboServiceService.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ComboService> detail(@PathVariable("id") String id) {
        ComboService comboService = comboServiceService.findById(id);
        return new ResponseEntity<ComboService>(comboService, HttpStatus.OK);
    }


    @PostMapping("/save")
    public ResponseEntity<ComboService> save(
            @RequestBody ComboService comboService
    ) {
        comboService.setCreateAt(new Date());
        comboService.setUpdateAt(new Date());
        comboService.setStatus(1);
        comboServiceService.add(comboService);
        return new ResponseEntity<ComboService>(comboService, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ComboService> update(
            @PathVariable("id") String id,
            @RequestBody ComboService comboService
    ) {
        comboService.setId(id);
        comboService.setUpdateAt(new Date());
        comboServiceService.add(comboService);
        return new ResponseEntity<ComboService>(comboService, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ComboService> detele(@PathVariable("id") String id) {
        ComboService comboService = comboServiceService.findById(id);
        comboService.setStatus(0);
        comboServiceService.add(comboService);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

}
