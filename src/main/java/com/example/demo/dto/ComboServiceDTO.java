package com.example.demo.dto;

import com.example.demo.entity.Combo;
import com.example.demo.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboServiceDTO {
    private String id;
    private Service service;
    private Combo combo;
    private BigDecimal price;
}
