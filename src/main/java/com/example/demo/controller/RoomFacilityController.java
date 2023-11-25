package com.example.demo.controller;

import com.example.demo.entity.RoomFacility;
import com.example.demo.service.RoomFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room-facility")
public class RoomFacilityController {

    @Autowired
    private RoomFacilityService roomFacilityService;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        System.out.println("Delete room facility: " + id);
        RoomFacility roomFacility = roomFacilityService.getById(id);

        if (roomFacility != null) {
            roomFacilityService.delete(roomFacility);
            return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error deleting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
