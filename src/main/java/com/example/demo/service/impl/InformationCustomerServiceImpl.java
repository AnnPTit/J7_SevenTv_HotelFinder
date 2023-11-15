package com.example.demo.service.impl;

import com.example.demo.entity.InformationCustomer;
import com.example.demo.repository.InformationCustomerRepository;
import com.example.demo.service.InformationCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationCustomerServiceImpl implements InformationCustomerService {

    @Autowired
    private InformationCustomerRepository informationCustomerRepository;

    @Override
    public List<InformationCustomer> getAll() {
        return informationCustomerRepository.findAll();
    }

    @Override
    public List<InformationCustomer> findAllByOrderDetailId(String id) {
        return informationCustomerRepository.findAllByOrderDetailId(id);
    }

    @Override
    public List<InformationCustomer> findAllByOrderId(String id) {
        return informationCustomerRepository.findAllByOrderId(id);
    }

    @Override
    public InformationCustomer add(InformationCustomer informationCustomer) {
        return informationCustomerRepository.save(informationCustomer);
    }

    @Override
    public InformationCustomer getById(String id) {
        return informationCustomerRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(InformationCustomer informationCustomer) {
        informationCustomerRepository.delete(informationCustomer);
    }

}
