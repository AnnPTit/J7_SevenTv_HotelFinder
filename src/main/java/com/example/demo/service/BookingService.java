package com.example.demo.service;

import com.example.demo.dto.BookingDTO;

public interface BookingService {
    BookingDTO create(BookingDTO bookingDTO);

    BookingDTO findOne(String id);
}
