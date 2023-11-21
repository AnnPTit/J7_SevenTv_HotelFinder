package com.example.demo.repository;

import com.example.demo.entity.Room;
import com.example.demo.entity.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomFacility, String> {

    List<RoomFacility> getRoomFacilitiesByRoomId(String id);

    void deleteRoomFacilitiesByRoom(Room room);

}
