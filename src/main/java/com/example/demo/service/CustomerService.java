package com.example.demo.service;

import com.example.demo.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAll();

    Customer getOne(String id);

    Customer add(Customer customer);

    Customer update(Customer customer);

    void remove(String id);

}
