package com.example.demo.controller;

import com.example.demo.entity.Facility;
import com.example.demo.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/load")
    public List<Facility> getAll() {
        return facilityService.getAll();
    }

}
