package com.example.demo.dto;

import com.example.demo.entity.ComboService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboDTO {
    private String id;
    private String comboCode;
    private String comboName;
    private BigDecimal price;
    private String note;
    private List<ComboService> comboServiceList;
}
