package com.example.demo.service.impl;

import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Page<Room> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
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
}
