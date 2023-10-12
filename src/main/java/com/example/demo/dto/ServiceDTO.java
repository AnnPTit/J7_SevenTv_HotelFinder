package com.example.demo.dto;

import com.example.demo.entity.ServiceType;
import com.example.demo.entity.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {
    private String id;
    private ServiceType serviceType;
    private Unit unit;
    private String serviceCode;
    private String serviceName;
    private BigDecimal price;
    private String description;

}
