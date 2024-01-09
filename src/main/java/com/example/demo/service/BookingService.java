package com.example.demo.service;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    Page<Booking> findAll(Pageable pageable);

    BookingDTO create(Booking booking);

    Booking findOne(String id);

    Booking getById(String id);

    Booking update(Booking booking);

    Booking getByIdOrder(String idOrder);

    Booking getNumberRoomBooked(String typeRoomId , String checkIn , String checkOut);

}
