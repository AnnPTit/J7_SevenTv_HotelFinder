package com.example.demo.service;

import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {

    List<Room> getAllByStatus();

    List<Room> getList();

    Page<Room> getAll(Pageable pageable);

    Page<Room> loadAndSearch(String roomCode, String roomName, String floorId, String typeRoomId, Pageable pageable);

    Page<Room> loadAndSearchForHome(String roomCode, String roomName, String floorId, String typeRoomId, String id, Pageable pageable);


    Room getRoomById(String id);

    Room add(Room room);

    void delete(String id);

    boolean existsByRoomCode(String code);

    boolean existsByRoomName(String name);

}
