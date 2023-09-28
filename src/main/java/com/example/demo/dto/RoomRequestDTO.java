package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomRequestDTO {
    private String roomName;
    private String typeRoom;
    private int numberCustom;
    private BigDecimal pricePerHours;
    private BigDecimal pricePerDay;
    private Date checkIn;
    private Date checkOut;
}
