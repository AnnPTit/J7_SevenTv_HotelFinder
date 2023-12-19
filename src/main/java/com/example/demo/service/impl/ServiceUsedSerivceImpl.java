package com.example.demo.service.impl;

import com.example.demo.entity.ServiceUsed;
import com.example.demo.repository.ServiceUsedRepository;
import com.example.demo.service.ServiceUsedSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceUsedSerivceImpl implements ServiceUsedSerivce {

    @Autowired
    private ServiceUsedRepository serviceUsedRepository;

    @Override
    public List<ServiceUsed> getAll() {
        return serviceUsedRepository.findAll();
    }

    @Override
    public List<ServiceUsed> getAllByOrderDetailId(String id) {
        return serviceUsedRepository.getAllByOrderDetailId(id);
    }

    @Override
    public ServiceUsed getById(String id) {
        return serviceUsedRepository.findById(id).orElse(null);
    }

    @Override
    public ServiceUsed add(ServiceUsed serviceUsed) {
        return serviceUsedRepository.save(serviceUsed);
    }

    @Override
    public void delete(ServiceUsed serviceUsed) {
        serviceUsedRepository.delete(serviceUsed);
    }

    @Override
    public ServiceUsed getByService(String service, String orderDetail) {
        return serviceUsedRepository.getByService(service, orderDetail);
    }

    @Override
    public void updateQuantityServiceUsed(Integer quantity, String serviceId) {
        serviceUsedRepository.updateQuantityServiceUsed(quantity, serviceId);
    }

    @Override
    public void updateQuantity(Integer quantity, String serviceId) {
        serviceUsedRepository.updateQuantity(quantity, serviceId);
    }

}
