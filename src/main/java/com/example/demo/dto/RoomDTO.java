package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomDTO {
    private Long id;
    private String roomCode;
    private String roomName;
    private String typeRoom;
    private int numberCustom;
    private BigDecimal pricePerHours;
    private BigDecimal pricePerDay;
    private String note;
    private List<String> urls;
}
