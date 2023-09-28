package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBookingDTO {
    private String roomCode ;
    private String roomName ;
    private String note ;
    private String typeRoom ;
    private Integer capacity ;

}
