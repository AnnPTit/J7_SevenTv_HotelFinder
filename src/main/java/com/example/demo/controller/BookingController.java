package com.example.demo.controller;

import com.example.demo.dto.BookingDTO;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/home/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping("/{id}")
    public BookingDTO findOne(@PathVariable("id") String id) {
        System.out.println("heelo");
        return bookingService.findOne(id);
    }
}
