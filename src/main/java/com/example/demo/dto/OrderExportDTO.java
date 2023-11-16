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
    private BigDecimal surcharge;


    public String getMonneyCustom() {
        return DataUtil.currencyFormat(monneyCustom);
    }

    public String getDeposit() {
        return DataUtil.currencyFormat(deposit);
    }

    public String getExcessMoney() {
        return DataUtil.currencyFormat(excessMoney);
    }

    public String getSurcharge() {
        return DataUtil.currencyFormat(surcharge);
    }
}
