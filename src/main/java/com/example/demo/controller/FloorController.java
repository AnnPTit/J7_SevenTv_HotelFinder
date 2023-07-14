package com.example.demo.controller;

import com.example.demo.entity.Floor;
import com.example.demo.service.FloorService;
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
@RequestMapping("/api/floor")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @GetMapping("/load")
    public List<Floor> getAll() {
        return floorService.getAll();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Floor> detail(@PathVariable("id") String id) {
        Floor floor = floorService.getFloorById(id);
        return new ResponseEntity<Floor>(floor, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Floor> save(@RequestBody Floor floor) {
        floor.setCreateAt(new Date());
        floor.setUpdateAt(new Date());
        floor.setStatus(1);
        floorService.add(floor);
        return new ResponseEntity<Floor>(floor, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Floor> save(@PathVariable("id") String id, @RequestBody Floor floor) {
        Floor fl = floorService.getFloorById(id);
        fl.setFloorCode(floor.getFloorCode());
        fl.setFloorName(floor.getFloorName());
        fl.setNote(floor.getNote());
        fl.setUpdateAt(new Date());
        floorService.add(fl);
        return new ResponseEntity<Floor>(fl, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        floorService.delete(id);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
