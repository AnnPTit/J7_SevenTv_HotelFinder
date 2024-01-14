package com.example.demo.repository.custom;

import com.example.demo.dto.BookingRequest;
import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {
    Page<Booking> search(BookingRequest bookingRequest, Pageable pageable);
}
