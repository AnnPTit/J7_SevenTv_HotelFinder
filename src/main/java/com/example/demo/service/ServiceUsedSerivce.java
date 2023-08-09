package com.example.demo.service;

import com.example.demo.entity.ServiceUsed;

import java.util.List;

public interface ServiceUsedSerivce {

    List<ServiceUsed> getAll();

    List<ServiceUsed> getAllByOrderDetailId(String id);

    ServiceUsed getById(String id);

    ServiceUsed add(ServiceUsed serviceUsed);

    void delete(ServiceUsed serviceUsed);

}
