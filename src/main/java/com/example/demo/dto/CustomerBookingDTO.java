package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerBookingDTO {

    private String idBooking;
    private String idOrder;
    private String idCustomer;
    private String citizenId;
    private Boolean gender;
    private Date birthday;
    private String nationality;
    private String address;

}
