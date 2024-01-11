package com.example.demo.dto;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Order;
import com.example.demo.entity.TypeRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTO {
    private String id;
    private TypeRoom typeRoom;
    private Customer customer;
    private Order order;
    private String note;
    private Integer numberRooms;
    private Integer numberAdults;
    private Integer numberChildren;
    private Integer numberDays;
    private Date checkInDate;
    private Date checkOutDate;
    private BigDecimal roomPrice;
    private BigDecimal vat;
    private BigDecimal totalPrice;
    private String bankAccountNumber;
    private String bankAccountName;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;
    private String url;

}
