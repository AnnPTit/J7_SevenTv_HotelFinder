package com.example.demo.dto;

import com.example.demo.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentMethodDTO {

    private String id;
    private Order order;
    private String paymentMethodCode;
    private Boolean method;
    private BigDecimal totalMoney;
    private Date createAt;
    private Date updateAt;
    private Integer status;

}
