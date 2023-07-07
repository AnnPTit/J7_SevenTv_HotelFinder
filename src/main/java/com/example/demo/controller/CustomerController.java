package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/load")
    public List<Customer> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/detail/{id}")
    public Customer detail(@PathVariable("id") String id) {
        return customerService.getOne(id);
    }

    @PostMapping("/save")
    public Customer save(@RequestBody Customer customer) {
        return customerService.add(customer);
    }

    @PutMapping("/update/{id}")
    public Customer update(@RequestBody Customer customer, @PathVariable("id") String id) {
        customer.setId(id);
        customer.setUpdateAt(new Date());
        return customerService.update(customer);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Customer> delete(@PathVariable("id") String id) {
        customerService.remove(id);
        return new ResponseEntity<Customer>(HttpStatus.OK);
    }

}
