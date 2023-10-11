package com.example.demo.service;

import com.example.demo.entity.ServiceUsed;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceUsedSerivce {

    List<ServiceUsed> getAll();

    List<ServiceUsed> getAllByOrderDetailId(String id);

    ServiceUsed getById(String id);

    ServiceUsed add(ServiceUsed serviceUsed);

    void delete(ServiceUsed serviceUsed);

    ServiceUsed getByService(String service, String orderDetail);

    void updateQuantityServiceUsed(Integer quantity, String serviceId);

}
