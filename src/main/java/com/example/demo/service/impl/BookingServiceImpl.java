package com.example.demo.service.impl;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.Booking;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.BookingService;
import com.example.demo.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public BookingDTO create(Booking booking) {
        bookingRepository.save(booking);
        return bookingMapper.toDTO(booking);
    }

    @Override
    public Booking findOne(String id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking getNumberRoomBooked(String typeRoomId, String checkIn, String checkOut) {
        LocalDate checkInTime = DataUtil.convertStringToLocalDate(checkIn);
        LocalDate checkOutTime = DataUtil.convertStringToLocalDate(checkOut);
        Date ci = DataUtil.convertLocalDateToDateWithTime(checkInTime, 14);
        Date co = DataUtil.convertLocalDateToDateWithTime(checkOutTime, 12);
        bookingRepository.checkRoomBooked(typeRoomId, ci, co);
        return null;
    }
}
