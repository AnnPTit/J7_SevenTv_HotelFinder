package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/general")
public class ApiGeneralController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/getCustomerDifferenceOrder/{orderId}/{orderDetailId}")
    public List<Customer> getCustomerDifferentOrder(@PathVariable("orderId") String orderId,
                                                    @PathVariable("orderDetailId") String orderDetailId) {
        return customerService.getCustomerDifferentOrder(orderId, orderDetailId);
    }

}
