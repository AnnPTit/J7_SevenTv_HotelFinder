package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderExportDTO {
    private String code;
    private String creater;
    private String customer;
    private Date bookingDay;
    private Date checkIn;
    private Date checkOut;
    private String note;
    private BigDecimal monneyCustom;
    private BigDecimal deposit;
    private BigDecimal vat;
    private BigDecimal totalMoney;
    private BigDecimal excessMoney;
}
