package com.example.demo.mapper.impl;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.entity.Service;
import com.example.demo.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceMapperImpl implements EntityMapper<ServiceDTO, Service> {

    @Override
    public Service toEntity(ServiceDTO dto) {
        return null;
    }

    @Override
    public ServiceDTO toDto(Service entity) {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(entity.getId());
        serviceDTO.setServiceType(entity.getServiceType());
        serviceDTO.setUnit(entity.getUnit());
        serviceDTO.setServiceCode(entity.getServiceCode());
        serviceDTO.setServiceName(entity.getServiceName());
        serviceDTO.setPrice(entity.getPrice());
        serviceDTO.setDescription(entity.getDescription());
        return serviceDTO;
    }

    @Override
    public List<Service> toEntity(List<ServiceDTO> dtoList) {
        return null;
    }

    @Override
    public List<ServiceDTO> toDto(List<Service> entityList) {
        List<ServiceDTO> serviceDTOs = new ArrayList<>();
        for (Service service : entityList) {
            serviceDTOs.add(toDto(service));
        }
        return serviceDTOs;
    }
}
