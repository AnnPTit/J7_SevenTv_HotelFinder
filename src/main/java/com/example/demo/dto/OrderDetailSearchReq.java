package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailSearchReq {
    private List<String> idsRoom;
    private Date dayStart;
    private Date dayEnd;
}
