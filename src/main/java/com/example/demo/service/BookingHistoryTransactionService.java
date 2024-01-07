package com.example.demo.service;

import com.example.demo.dto.BookingHistoryTransactionDTO;
import com.example.demo.entity.BookingHistoryTransaction;

public interface BookingHistoryTransactionService {
    BookingHistoryTransaction create(BookingHistoryTransaction bookingHistoryTransaction);
}
