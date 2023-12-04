package com.example.demo.controller;

import com.example.demo.constant.Constant;
import com.example.demo.dto.DiscountProgramDTO;
import com.example.demo.entity.DiscountProgram;
import com.example.demo.service.DiscountProgramService;
import com.example.demo.util.BaseService;
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
@RequestMapping("/api/admin/discount-program")

public class DiscountProgramController {

    @Autowired
    private DiscountProgramService discountProgramService;

    @Autowired
    private BaseService baseService;

    @GetMapping("/getOne/{id}")
    public DiscountProgram getOne(@PathVariable("id") String id) {
        return discountProgramService.getOne(id);
    }

    @GetMapping("/loadAndSearch")
    public Page<DiscountProgramDTO> loadAndSearch(@RequestParam(name = "key", defaultValue = "") String key,
                                                  @RequestParam(name = "current_page", defaultValue = "0") int current_page
    ) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return discountProgramService.loadAndSearch(key, key, pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<DiscountProgram> detail(@PathVariable("id") String id) {
        return ResponseEntity.ok(discountProgramService.findById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<DiscountProgram> add(@Valid @RequestBody DiscountProgram discountProgram,
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
        discountProgram.setCode(discountProgramService.generateDiscountProgramCode());
        discountProgram.setCreateAt(new Date());
        discountProgram.setUpdateAt(new Date());
        discountProgram.setCreateBy(baseService.getCurrentUser().getFullname());
        discountProgram.setUpdatedBy(baseService.getCurrentUser().getFullname());
        discountProgram.setStatus(Constant.COMMON_STATUS.ACTIVE);
        discountProgramService.add(discountProgram);
        return new ResponseEntity<DiscountProgram>(discountProgram, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DiscountProgram> update(@PathVariable("id") String id,
                                                  @Valid @RequestBody DiscountProgram discountProgram,
                                                  BindingResult result) {
        discountProgram.setId(id);
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        discountProgram.setUpdateAt(new Date());
        discountProgram.setUpdatedBy(baseService.getCurrentUser().getFullname());
        discountProgramService.add(discountProgram);
        return new ResponseEntity<DiscountProgram>(discountProgram, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DiscountProgram> delete(@PathVariable("id") String id) {
        DiscountProgram discountProgram = discountProgramService.findById(id);
        discountProgram.setStatus(Constant.COMMON_STATUS.UNACTIVE);
        discountProgramService.add(discountProgram);
        return new ResponseEntity<DiscountProgram>(discountProgram, HttpStatus.OK);
    }


}
