package com.example.demo.service;

import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFacility;

import java.util.List;

public interface RoomFacilityService {

    List<RoomFacility> getRoomFacilitiesByRoomId(String id);

    RoomFacility save(RoomFacility roomFacility);

    void deleteRoomFacilitiesByRoom(Room room);

}
