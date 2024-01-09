package com.example.demo.service;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    Page<Booking> findAll(Pageable pageable);

    BookingDTO create(Booking booking);

    Booking findOne(String id);

    Booking getNumberRoomBooked(String typeRoomId , String checkIn , String checkOut);

    List<Booking> getAllByStatus(Integer status, String idCuss);
}
