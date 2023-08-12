package com.example.demo.service;

import com.example.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Page<Customer> getAll(Pageable pageable);

    List<Customer> getList();

    Customer getOne(String id);

    Customer findByCustomerCode(String code);

    Optional<Customer> findCustomerByEmail(String email);

    Customer add(Customer customer);

    Customer update(Customer customer);

    void remove(String id);

    Customer getCustomerByCode();

}
