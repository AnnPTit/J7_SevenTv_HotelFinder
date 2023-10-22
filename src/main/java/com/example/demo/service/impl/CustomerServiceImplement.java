package com.example.demo.service.impl;

import com.example.demo.dto.CustomerLoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerNotFondException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    public List<Customer> findAllByStatus(String citizenId, String fullname, String phoneNumber) {
        return customerRepository.getAllByStatus(
                (citizenId != null && !citizenId.isEmpty()) ? "%" + citizenId + "%" : null,
                (fullname != null && !fullname.isEmpty()) ? "%" + fullname + "%" : null,
                (phoneNumber != null && !phoneNumber.isEmpty()) ? "%" + phoneNumber + "%" : null);
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
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
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
        String kh = "KH";
        kh += String.format("%2d", customerRepository.getAllCustomer().size() + 1);
        return kh;
    }

    public Customer getCustomerById(String id) {
        return customerRepository.getCustomerById(id);
    }

    @Override
    public List<Customer> getAllCustomer(String id) {
        return customerRepository.getAllCustomer(id);
    }

    @Override
    public List<Customer> getAllCustomerByOrderDetailId(String id) {
        return customerRepository.getAllCustomerByOrderDetailId(id);
    }

    @Override
    public List<Customer> getCustomer() {
        return customerRepository.getAllCustomer();
    }

    @Override
    public Long countCustomerByStatus() {
        return customerRepository.countCustomerByStatus();
    }

    @Override
    public Customer login(CustomerLoginDTO customerLoginDTO) {
        Customer customer = customerRepository.findCustomerByEmail(customerLoginDTO.getEmail()).orElse(null);
        if (Objects.isNull(customer)) {
            throw new CustomerNotFondException();
        }
        if (!customer.getPassword().equals(customerLoginDTO.getPassword())) {
            throw new CustomerNotFondException();
        }
        return customer;

    }

}
