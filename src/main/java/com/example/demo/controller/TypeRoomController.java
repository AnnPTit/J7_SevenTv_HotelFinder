package com.example.demo.controller;

import com.example.demo.entity.Floor;
import com.example.demo.entity.TypeRoom;
import com.example.demo.service.TypeRoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/type-room")
public class TypeRoomController {

    @Autowired
    private TypeRoomService typeRoomService;

    @GetMapping("/getList")
    public List<TypeRoom> getList() {
        return typeRoomService.getList();
    }

    @GetMapping("/load")
    public Page<TypeRoom> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return typeRoomService.getAll(pageable);
    }

    @GetMapping("/search")
    public Page<TypeRoom> findByCodeOrName(@RequestParam(name = "key") String key,
                                       @RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        if (key == "") {
            return typeRoomService.getAll(pageable);
        }

        return typeRoomService.findByCodeOrName(key, pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TypeRoom> detail(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<TypeRoom> save(@Valid @RequestBody TypeRoom typeRoom, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        if (typeRoom.getTypeRoomCode().trim().isEmpty() || typeRoom.getTypeRoomName().trim().isEmpty()
                || typeRoom.getNote().trim().isEmpty()) {
            return new ResponseEntity("Not Empty", HttpStatus.BAD_REQUEST);
        }
        if (typeRoomService.existsByCode(typeRoom.getTypeRoomCode())) {
            return new ResponseEntity("Type Room Code is exists !", HttpStatus.BAD_REQUEST);
        }
        typeRoom.setCreateAt(new Date());
        typeRoom.setUpdateAt(new Date());
        typeRoom.setStatus(1);
        typeRoomService.add(typeRoom);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TypeRoom> save(@PathVariable("id") String id,
                                         @Valid @RequestBody TypeRoom typeRoom,
                                         BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getField();
                String value = error.getDefaultMessage();
                errorMap.put(key, value);
            }
            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
        }
        typeRoom.setId(id);
        typeRoom.setUpdateAt(new Date());
        typeRoomService.add(typeRoom);
        return new ResponseEntity<TypeRoom>(typeRoom, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        TypeRoom typeRoom = typeRoomService.getTypeRoomById(id);
        typeRoom.setStatus(0);
        typeRoomService.add(typeRoom);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
