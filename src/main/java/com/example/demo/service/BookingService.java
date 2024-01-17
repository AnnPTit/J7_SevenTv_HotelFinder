package com.example.demo.service;

import com.example.demo.dto.BookingDTO;
import com.example.demo.dto.BookingRequest;
import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    Page<Booking> findAll(BookingRequest request, Pageable pageable);

    BookingDTO create(Booking booking);

    Booking findOne(String id);

    Booking getById(String id);

    Booking update(Booking booking);

    Booking getByIdOrder(String idOrder);

    Booking getNumberRoomBooked(String typeRoomId, String checkIn, String checkOut);

    List<Booking> getAllByStatus(Integer status, String idCuss);

    boolean cancel(String id, String reason);

    boolean confirmCancel(String id);

    List<Booking> loadNotify();
}
