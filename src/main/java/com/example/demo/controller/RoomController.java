package com.example.demo.controller;

import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/load")
    public List<Room> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<Room> page = roomService.getAll(pageable);
        return page.getContent();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Room> save(@RequestBody Room room) {
        room.setCreateAt(new Date());
        room.setUpdateAt(new Date());
        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Room> save(@PathVariable("id") String id, @RequestBody Room room) {
        room.setId(id);
        room.setUpdateAt(new Date());
        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        roomService.delete(id);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }
    
}
