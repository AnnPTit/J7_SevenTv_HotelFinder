package com.example.demo.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class RoomData {
    private String id;
    private int guestCount;
}
