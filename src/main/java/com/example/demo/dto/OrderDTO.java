package com.example.demo.dto;

import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDTO {

    private String id;
    private Date bookingDateStart;
    private Date bookingEndStart;
    private BigDecimal totalMoney;
    private String note;
    private BigDecimal vat;
    private BigDecimal moneyGivenByCustomer;
    private BigDecimal excessMoney;
    private BigDecimal deposit;
    private String customerId;
    private String idReturn;
    private Integer status;
    private Customer customer;
    private Account account;
    private String deleted;
    private List<OrderDetail> orderDetailList;

}
