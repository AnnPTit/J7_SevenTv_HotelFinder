package com.example.demo.service;

import com.example.demo.entity.BookingHistoryTransaction;

import java.util.List;

public interface BookingHistoryTransactionService {
    BookingHistoryTransaction create(BookingHistoryTransaction bookingHistoryTransaction);

    List<BookingHistoryTransaction> getByIdBooking(String id);
}
