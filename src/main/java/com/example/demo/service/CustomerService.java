package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Page<Customer> getAll(Pageable pageable);

    List<Customer> findAll();

    Page<Customer> loadAndSearch(String customerCode, String fullname, String phoneNumber, Pageable pageable);

    Customer findById(String id);

    Boolean add(Customer customer);

    Boolean delete(String id);

    Optional<Customer> findByEmail(String email);

    Customer findByCitizenId(String citizenId);

    Customer getCustomertByCode();

    String generateCustomerCode();

}
