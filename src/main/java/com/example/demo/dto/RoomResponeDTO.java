package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponeDTO {
    private String room_code ;
    private String room_name ;
    private String note ;
    private String typeRoom ;
    private Integer capacity ;
}
