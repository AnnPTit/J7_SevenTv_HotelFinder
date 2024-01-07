package com.example.demo.dto;

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
    private String idTypeRoom;
    private String idCustomer;
    private String idOrder;
    private String note;
    private Integer numberRooms;
    private Integer numberCustomers;
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
}
