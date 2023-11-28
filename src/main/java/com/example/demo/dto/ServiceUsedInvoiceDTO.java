package com.example.demo.dto;

import com.example.demo.util.DataUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUsedInvoiceDTO {
    private String roomName1;
    private String service;
    private Integer quantity2;
    private BigDecimal price;
    private BigDecimal total2;
    private Date createAt;

    public String getPrice() {
        return DataUtil.currencyFormat(this.price);
    }

    public String getTotal2() {
        return DataUtil.currencyFormat(this.total2);
    }

    public BigDecimal getTotal3() {
        return this.total2;
    }

    public String getCreateAt() {
        return DataUtil.dateToString2(this.createAt);
    }

}
