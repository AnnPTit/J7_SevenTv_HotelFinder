package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryTransactionDTO {
    private String id;
    private String idBooking;
    private BigDecimal totalPrice;
    private Integer paymentMethod;
    private Integer type;
    private String note;
    private String cancelReason;
    private Date cancelDate;
    private BigDecimal refundMoney;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;
}
