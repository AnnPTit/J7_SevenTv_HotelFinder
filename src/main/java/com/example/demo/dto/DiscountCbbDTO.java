package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCbbDTO {
    private String id;
    private String name;
    private BigDecimal minimumInvoice; // Giá trị hóa đơn tối thiểu
    private BigDecimal maximumDiscountMoney; // Số tiền giảm tối đa
    private BigDecimal reduceValue; // Phần trăm giảm
    private Integer numberOfApplication; // Số lần áp dụng
}
