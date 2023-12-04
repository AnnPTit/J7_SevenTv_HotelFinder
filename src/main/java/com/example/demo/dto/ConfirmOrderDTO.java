package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmOrderDTO {
    private String citizenId;
    private String birthday;
    private boolean gender;
    private String address;
    private String nation;
    private String orderId;
    private String customerId;
    private String message;
    private Boolean isNewCustomer;
}
