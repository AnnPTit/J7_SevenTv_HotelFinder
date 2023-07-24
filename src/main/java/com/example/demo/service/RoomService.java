package com.example.demo.service;

import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    Page<Room> getAll(Pageable pageable);

    Page<Room> loadAndSearch(String roomCode, String roomName, String floorId, String typeRoomId, Pageable pageable);

    Room getRoomById(String id);

    Room add(Room room);

    void delete(String id);

    boolean existsByCode(String code);

}
