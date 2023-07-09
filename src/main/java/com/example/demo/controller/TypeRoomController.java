package com.example.demo.controller;

import com.example.demo.entity.Floor;
import com.example.demo.entity.TypeRoom;
import com.example.demo.service.TypeRoomService;
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
@RequestMapping("/api/type-room")
public class TypeRoomController {

    @Autowired
    private TypeRoomService typeRoomService;

    @GetMapping("/load")
    public List<TypeRoom> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<TypeRoom> page = typeRoomService.getAll(pageable);
        return page.getContent();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TypeRoom> detail(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<TypeRoom> save(@RequestBody TypeRoom typeRoom) {
        typeRoom.setCreateAt(new Date());
        typeRoom.setUpdateAt(new Date());
        typeRoomService.add(typeRoom);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TypeRoom> save(@PathVariable("id") String id, @RequestBody TypeRoom typeRoom) {
        typeRoom.setId(id);
        typeRoom.setUpdateAt(new Date());
        typeRoomService.add(typeRoom);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        typeRoomService.delete(id);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
