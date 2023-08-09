package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDTO {

    private String id;
    private BigDecimal totalMoney;
    private String note;
    private BigDecimal vat;
    private BigDecimal moneyGivenByCustomer;
    private BigDecimal excessMoney;

}
