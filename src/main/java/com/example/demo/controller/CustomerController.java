package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
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
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/load")
    public List<Customer> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<Customer> page = customerService.getAll(pageable);
        return page.getContent();
    }

    @GetMapping("/detail/{id}")
    public Customer detail(@PathVariable("id") String id)    {
        return customerService.getOne(id);
    }

    @PostMapping("/save")
    public ResponseEntity save(@Valid @RequestBody Customer customer,
                               BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorMessages);
        } else {
            customerService.add(customer);
            return ResponseEntity.ok("Customer added successfully");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@Valid @RequestBody Customer customer, @PathVariable("id") String id,
                                 BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorMessages);
        } else {
            customer.setId(id);
            customer.setUpdateAt(new Date());
            customerService.update(customer);
            return ResponseEntity.ok("Customer updated successfully");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Customer> delete(@PathVariable("id") String id) {
        customerService.remove(id);
        return new ResponseEntity<Customer>(HttpStatus.OK);
    }

}
