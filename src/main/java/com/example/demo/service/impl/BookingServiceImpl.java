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
import java.util.Date;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findAll(String customerFullname, String customerPhone, String customerEmail, Integer status, Pageable pageable) {
        return bookingRepository.findAll((customerFullname != null && !customerFullname.isEmpty()) ? customerFullname : null,
                (customerPhone != null && !customerPhone.isEmpty()) ? customerPhone : null,
                (customerEmail != null && !customerEmail.isEmpty()) ? customerEmail : null,
                (status != null && !status.toString().isEmpty()) ? status : null, pageable);
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

    @Override
    public Booking getById(String id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getByIdOrder(String idOrder) {
        return bookingRepository.getByIdOrder(idOrder);
    }

    public List<Booking> getAllByStatus(Integer status, String idCuss) {
        return bookingRepository.getAllByStatus(status, idCuss);
    }
}
