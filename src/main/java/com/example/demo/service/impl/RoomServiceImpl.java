package com.example.demo.service.impl;

import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> getList() {
        return roomRepository.findAll();
    }

    @Override
    public Page<Room> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    public Page<Room> loadAndSearch(String roomCode, String roomName, String floorId, String typeRoomId, Pageable pageable) {
        return roomRepository.loadAndSearch(
                (roomCode != null && !roomCode.isEmpty()) ? roomCode : null,
                (roomName != null && !roomName.isEmpty()) ? "%" + roomName + "%" : null,
                (floorId != null && !floorId.isEmpty()) ? floorId : null,
                (typeRoomId != null && !typeRoomId.isEmpty()) ? typeRoomId : null,
                pageable
        );
    }

    @Override
    public Room getRoomById(String id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public Room add(Room room) {
        try {
            return roomRepository.save(room);
        } catch (Exception e) {
            System.out.println("Add error!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            roomRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public boolean existsByRoomCode(String code) {
        return roomRepository.existsByRoomCode(code);
    }

    @Override
    public boolean existsByRoomName(String name) {
        return roomRepository.existsByRoomName(name);
    }
}
