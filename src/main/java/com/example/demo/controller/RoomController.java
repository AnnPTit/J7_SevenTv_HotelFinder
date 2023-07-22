package com.example.demo.controller;

import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.service.PhotoService;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private PhotoService photoService;

    @GetMapping("/load")
    public Page<Room> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        return roomService.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Room> detail(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Room> add(@Valid @RequestBody Room room,
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
        room.setCreateAt(new Date());
        room.setUpdateAt(new Date());
        room.setStatus(1);
        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Room> save(@ModelAttribute Room room, @PathParam("photos") MultipartFile[] photos) {
        List<Photo> photoList = new ArrayList<>();
        savePicture(room, photos, photoList);
        System.out.println("PhotoList:" + photoList.toString());
//        room.setPhotoList(photoList);
//        room.setCreateAt(new Date());
//        room.setUpdateAt(new Date());
//        room.setStatus(1);
//        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Room> update(@PathVariable("id") String id, @RequestBody Room room) {
        room.setId(id);
        room.setUpdateAt(new Date());
        roomService.add(room);
        return new ResponseEntity<Room>(room, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        Room room = roomService.getRoomById(id);
        room.setStatus(0);
        roomService.add(room);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

    private void savePicture(@ModelAttribute Room room,
                             @PathParam("photos") MultipartFile[] photos,
                             List<Photo> photoList) {
        if (photos != null && photos.length > 0) {
            // Save the Room entity first
            room.setCreateAt(new Date());
            room.setUpdateAt(new Date());
            room.setStatus(1);
            roomService.add(room);

            for (MultipartFile photoFile : photos) {
                if (!photoFile.isEmpty()) {
                    try {
                        String fileName = photoFile.getOriginalFilename();
                        String filePath = "D:/J7_HangNT169/src/main/resources/static/assets/img/room/" + fileName;
                        String filePathForSql = "/assets/img/room/" + fileName;
                        File dest = new File(filePath);
                        // Luu hinh anh vao thu muc
                        photoFile.transferTo(dest);
                        System.out.println(filePath);
                        // Tao doi tuong hinh
                        Photo photo = new Photo();
                        String id = UUID.randomUUID().toString();
                        photo.setId(id);
                        // Set the managed Room entity to the Photo
                        photo.setRoom(room);
                        photo.setUrl(filePathForSql);
                        photo.setCreateAt(new Date());
                        photo.setUpdateAt(new Date());
                        photo.setStatus(1);
                        // add hinh vao list
                        photoList.add(photo);
                    } catch (Exception e) {
                        // Xử lý lỗi khi lưu file
                        e.printStackTrace();
                    }
                }
            }
            // Save the list of Photo entities
            photoService.save(photoList);
        }
    }
}
