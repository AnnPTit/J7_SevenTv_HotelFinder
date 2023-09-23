package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImplement implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<Customer> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.getAll();
    }

    @Override
    public Page<Customer> loadAndSearch(String customerCode, String fullname, String phoneNumber, Pageable pageable) {
        return customerRepository.loadAndSearch(
                (customerCode != null && !customerCode.isEmpty()) ? customerCode : null,
                (fullname != null && !fullname.isEmpty()) ? "%" + fullname + "%" : null,
                (phoneNumber != null && !phoneNumber.isEmpty()) ? phoneNumber : null,
                pageable
        );
    }

    @Override
    public Customer findById(String id) {
        return customerRepository.findById(id).orElse(null);
    }

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
//            customer.setPassword(passwordEncoder.encode(customer.getPassword()));

            return customerRepository.save(customer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean delete(String id) {
        try {
            customerRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer findByCitizenId(String citizenId) {
        return customerRepository.findByCitizenId(citizenId);
    }

    @Override
    public Customer findCustomerByCode(String code) {
        return customerRepository.findByCustomerCode(code);
    }

    @Override
    public Customer getCustomertByCode() {
        return customerRepository.getCustomerByCode();
    }

    @Override
    public String generateCustomerCode() {
        int CODE_LENGTH = 8;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }

    public Customer getCustomerById(String id) {
        return customerRepository.getCustomerById(id);
    }


}
