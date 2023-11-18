package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDTO {
    private String roomId;
    private String roomName;
    private String typeRoom;
    private Date bookingStart;
    private Date bookingEnd;
    private BigDecimal price;
    private Integer numberCustom;
    private Integer orderStatus;
    private Date bookingDay;
    private String url;
    private String orderCode;
    private BigDecimal deposit;
    private String refuseReason;
}
