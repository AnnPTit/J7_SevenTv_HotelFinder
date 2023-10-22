package com.example.demo.service;

import com.example.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Page<Customer> getAll(Pageable pageable);

    List<Customer> findAllByStatus(String citizenId, String fullname, String phoneNumber);

    Page<Customer> loadAndSearch(String customerCode, String fullname, String phoneNumber, Pageable pageable);

    Customer findById(String id);

    Optional<Customer> findCustomerByEmail(String email);

    Customer add(Customer customer);

    Boolean delete(String id);

    Optional<Customer> findByEmail(String email);

    Customer findByCitizenId(String citizenId);

    Customer findCustomerByCode(String code);

    Customer getCustomertByCode();

    String generateCustomerCode();

    Customer getCustomerById(String id);

    List<Customer> getAllCustomer(String id);

    List<Customer> getAllCustomerByOrderDetailId(String id);

    List<Customer> getCustomer();

    Long countCustomerByStatus();

}
