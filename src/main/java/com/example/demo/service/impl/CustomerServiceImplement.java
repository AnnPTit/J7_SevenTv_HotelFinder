package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImplement implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Page<Customer> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> getList() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getOne(String id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer findByCustomerCode(String code) {
        return customerRepository.findByCustomerCode(code);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    @Override
    public Customer add(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Customer update(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void remove(String id) {
        try {
            customerRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete Error");
            e.printStackTrace();
        }
    }

    @Override
    public Customer getCustomerByCode() {
        return customerRepository.getCustomerByCode();
    }

}
