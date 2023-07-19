package com.example.demo.controller;

import com.example.demo.entity.Service;
import com.example.demo.service.ServiceService;
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
@RequestMapping("/api/admin/service")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/load")
    public Page<Service> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return serviceService.getAll(pageable);
    }


    @GetMapping("/loadAndSearch")
    public Page<Service> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                       @RequestParam(name = "serviceTypeId", defaultValue = "") String serviceTypeId,
                                       @RequestParam(name = "unitId", defaultValue = "") String unitId,
                                       @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return serviceService.loadAndSearch(key, key, serviceTypeId, unitId, pageable);

    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Service> detail(@PathVariable("id") String id) {
        Service service = serviceService.findById(id);
        return new ResponseEntity<Service>(service, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Service> save(@Valid
                                        @RequestBody Service service,
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
        if (serviceService.existsByCode(service.getServiceCode())) {
            return new ResponseEntity("Service Code is exists !", HttpStatus.BAD_REQUEST);
        }
        service.setCreateAt(new Date());
        service.setUpdateAt(new Date());
        service.setStatus(1);
        serviceService.add(service);
        return new ResponseEntity<Service>(service, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Service> delete(@PathVariable("id") String id) {
        Service service = serviceService.findById(id);

        service.setStatus(0);
        serviceService.add(service);
        return new ResponseEntity("Deleted", HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Service> update(
            @PathVariable("id") String id,
            @Valid
            @RequestBody Service Service,
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
        Service.setUpdateAt(new Date());
        serviceService.add(Service);
        return new ResponseEntity<Service>(Service, HttpStatus.OK);
    }


}
