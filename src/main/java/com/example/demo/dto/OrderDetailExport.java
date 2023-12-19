package com.example.demo.dto;

import com.example.demo.util.DataUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailExport {
    private String roomName;
    private String typeRoom;
    private Integer quantity;
    private Date checkIn;
    private Date checkOut;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public String getCheckIn() {return DataUtil.dateToString2(this.checkIn);}
    public String getCheckIn2() {return DataUtil.dateToString(this.checkIn);}

    public String getCheckOut() {
        return DataUtil.dateToString2(this.checkOut);
    }
    public String getCheckOut2() {
        return DataUtil.dateToString(this.checkOut);
    }

    public String getUnitPrice() {
        return DataUtil.currencyFormat(this.unitPrice);
    }

    public String getTotalPrice() {
        return DataUtil.currencyFormat(this.totalPrice);
    }

    public BigDecimal getTotalPrice2() {
        return this.totalPrice;
    }
}
