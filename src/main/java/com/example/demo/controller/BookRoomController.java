package com.example.demo.controller;

import com.example.demo.entity.BookRoom;
import com.example.demo.entity.Room;
import com.example.demo.service.BookRoomService;
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
@RequestMapping("/api/book-room")
public class BookRoomController {

    @Autowired
    private BookRoomService bookRoomService;

    @GetMapping("/getList")
    public List<BookRoom> getList() {
        return bookRoomService.getList();
    }

    @GetMapping("/load")
    public List<BookRoom> getAll(@RequestParam(name = "current_page", defaultValue = "0") int current_page) {
        Pageable pageable = PageRequest.of(current_page, 5);
        Page<BookRoom> page = bookRoomService.getAll(pageable);
        return page.getContent();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<BookRoom> detail(@PathVariable("id") String id) {
        BookRoom bookRoom = bookRoomService.getBookRoomById(id);
        return new ResponseEntity<BookRoom>(bookRoom, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<BookRoom> save(@RequestBody BookRoom bookRoom) {
        bookRoom.setCreateAt(new Date());
        bookRoom.setUpdateAt(new Date());
        bookRoomService.add(bookRoom);
        return new ResponseEntity<BookRoom>(bookRoom, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookRoom> save(@PathVariable("id") String id, @RequestBody BookRoom bookRoom) {
        bookRoom.setId(id);
        bookRoom.setUpdateAt(new Date());
        bookRoomService.add(bookRoom);
        return new ResponseEntity<BookRoom>(bookRoom, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) {
        bookRoomService.delete(id);
        return new ResponseEntity<String>("Deleted " + id + " successfully", HttpStatus.OK);
    }

}
