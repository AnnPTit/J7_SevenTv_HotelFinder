package com.example.demo.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class PayloadObject {
    private List<RoomData> rooms; // Thay đổi kiểu dữ liệu thành List<RoomData>
    private UserObject user;
    private Date dayStart;
    private Date dayEnd;
    private BigDecimal deposit;
    private BigDecimal totalPriceRoom;
    private String note;
}
