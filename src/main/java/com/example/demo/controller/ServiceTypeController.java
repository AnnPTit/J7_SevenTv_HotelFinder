package com.example.demo.controller;

import com.example.demo.entity.ServiceType;
import com.example.demo.service.ServiceTypeService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/service-type")
public class ServiceTypeController {

    @Autowired
    private ServiceTypeService serviceTypeServicel;

    @GetMapping("/load")
    public Page<ServiceType> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return serviceTypeServicel.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ServiceType> detail(@PathVariable("id") String id) {
        ServiceType serviceType = serviceTypeServicel.findById(id);
        return new ResponseEntity<ServiceType>(serviceType, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<ServiceType> save(@Valid
                                            @RequestBody ServiceType serviceType,
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
        if (serviceTypeServicel.existsByCode(serviceType.getServiceTypeCode())) {
            return new ResponseEntity("Service Type Code is exists !", HttpStatus.BAD_REQUEST);
        }
        serviceType.setCreateAt(new Date());
        serviceType.setUpdateAt(new Date());
        serviceType.setStatus(1);
        serviceTypeServicel.add(serviceType);
        return new ResponseEntity<ServiceType>(serviceType, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ServiceType> update(@Valid
                                              @RequestBody ServiceType serviceType,
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
        serviceType.setUpdateAt(new Date());
        serviceTypeServicel.add(serviceType);
        return new ResponseEntity<ServiceType>(serviceType, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ServiceType> delete(@PathVariable("id") String id) {
        ServiceType serviceType = serviceTypeServicel.findById(id);
        serviceType.setStatus(0);
        serviceTypeServicel.add(serviceType);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }
}
