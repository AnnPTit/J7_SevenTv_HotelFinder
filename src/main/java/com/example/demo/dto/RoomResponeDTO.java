package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponeDTO {
    private String id;
    private String roomCode;
    private String roomName;
    private String note;
    private String typeRoom;
    private Integer capacity;
    private BigDecimal pricePerHours;
    private BigDecimal pricePerDay;
    private List<String> urls;
    private Integer countBook;
}
