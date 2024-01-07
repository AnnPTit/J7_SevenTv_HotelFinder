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

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper ;
    @Autowired
    private BookingRepository bookingRepository ;

    @Override
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public BookingDTO create(BookingDTO bookingDTO) {
        Booking booking = bookingRepository.save(bookingMapper.toEntity(bookingDTO));
        return bookingMapper.toDTO(booking);
    }

    @Override
    public BookingDTO findOne(String id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if(!DataUtil.isNull(booking)){
            return bookingMapper.toDTO(booking);
        }
        return null;
    }
}
