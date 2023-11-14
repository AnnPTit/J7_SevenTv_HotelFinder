package com.example.demo.dto;

import com.example.demo.util.DataUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUsedInvoiceDTO {
    private String roomName1;
    private String service;
    private Integer quantity2;
    private BigDecimal price;
    private BigDecimal total;

    public String getPrice() {
        return DataUtil.currencyFormat(this.price);
    }

    public String getTotal() {
        return DataUtil.currencyFormat(this.total);
    }
}
