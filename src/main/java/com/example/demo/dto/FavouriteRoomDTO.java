package com.example.demo.dto;

import com.example.demo.entity.RoomFacility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavouriteRoomDTO {

    private String roomId;
    private String roomName;
    private String floor;
    private String typeRoom;
    private String typeRoomNote;
    private Integer capacity;
    private BigDecimal pricePerHours;
    private BigDecimal pricePerDay;
    private List<String> photoList;
    private List<RoomFacility> roomFacilityList;
}
