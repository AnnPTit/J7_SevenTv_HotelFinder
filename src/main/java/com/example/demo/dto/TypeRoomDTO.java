package com.example.demo.dto;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeRoomDTO {

    private String id;
    private String typeRoomCode;
    private String typeRoomName;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHours;
    private Integer capacity;
    private Integer adult;
    private Integer children;
    private String note;
    private Date createAt;
    private String createBy;
    private Date updateAt;
    private String updatedBy;
    private String deleted;
    private Integer status;
    private List<String> photoDTOS;
}
