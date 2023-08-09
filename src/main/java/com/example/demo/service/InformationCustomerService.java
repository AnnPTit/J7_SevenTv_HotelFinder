package com.example.demo.service;

import com.example.demo.entity.InformationCustomer;

import java.util.List;

public interface InformationCustomerService {

    List<InformationCustomer> findAllByOrderDetailId(String id);

    InformationCustomer add(InformationCustomer informationCustomer);

    InformationCustomer getById(String id);

    void delete(InformationCustomer informationCustomer);

}
