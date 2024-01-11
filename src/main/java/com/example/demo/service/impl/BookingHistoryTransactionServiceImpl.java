package com.example.demo.service.impl;

import com.example.demo.dto.BookingHistoryTransactionDTO;
import com.example.demo.entity.BookingHistoryTransaction;
import com.example.demo.repository.BookingHistoryTransactionRepository;
import com.example.demo.service.BookingHistoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingHistoryTransactionServiceImpl implements BookingHistoryTransactionService {
    @Autowired
    private BookingHistoryTransactionRepository bookingHistoryTransactionRepository;

    @Override
    public BookingHistoryTransaction create(BookingHistoryTransaction bookingHistoryTransaction) {
        return bookingHistoryTransactionRepository.save(bookingHistoryTransaction);

    }

    @Override
    public List<BookingHistoryTransaction> getByIdBooking(String id) {
        return bookingHistoryTransactionRepository.getByBookingId(id);
    }
}
