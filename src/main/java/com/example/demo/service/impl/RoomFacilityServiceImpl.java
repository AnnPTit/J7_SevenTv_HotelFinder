package com.example.demo.service.impl;

import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFacility;
import com.example.demo.repository.RoomFacilityRepository;
import com.example.demo.service.RoomFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomFacilityServiceImpl implements RoomFacilityService {

    @Autowired
    private RoomFacilityRepository roomFacilityRepository;

    @Override
    public List<RoomFacility> getRoomFacilitiesByRoomId(String id) {
        return roomFacilityRepository.getRoomFacilitiesByRoomId(id);
    }

    @Override
    public RoomFacility save(RoomFacility roomFacility) {
        return roomFacilityRepository.save(roomFacility);
    }

    @Override
    public void deleteRoomFacilitiesByRoom(Room room) {
        roomFacilityRepository.deleteRoomFacilitiesByRoom(room);
    }

}
