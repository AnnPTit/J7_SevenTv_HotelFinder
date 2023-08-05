package com.example.demo.dto;

import com.example.demo.entity.Order;
import com.example.demo.entity.Room;
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
public class OrderDetailDTO {

    private String id;
    private Order order;
    private Room room;
    private String orderDetailCode;
    private Date checkIn;
    private Date checkOut;
    private Integer timeIn;
    private BigDecimal roomPrice;
    private Integer customerQuantity;
    private Integer overdueTime;
    private String note;
    private Date createAt;
    private Date updateAt;
    private Integer status;

}
