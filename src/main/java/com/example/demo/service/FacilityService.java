package com.example.demo.service;

import com.example.demo.entity.Facility;

import java.util.List;

public interface FacilityService {

    List<Facility> getAll();

    Facility findById(String id);

}
