package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomCardDTO {
    private String id ;
    private String name  ;
    private String typeRoom ;
    private Integer capacity ;
    private Integer children ;
    private Integer bookingCount ;
    private BigDecimal price ;
    private String url ;
}
