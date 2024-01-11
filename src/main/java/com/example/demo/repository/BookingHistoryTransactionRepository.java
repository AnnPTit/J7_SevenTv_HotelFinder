package com.example.demo.repository;

import com.example.demo.entity.BookingHistoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryTransactionRepository extends JpaRepository<BookingHistoryTransaction, String> {

    @Query(value = "select * from `history_transaction _booking` htb where id_booking = :id and status = 0 ", nativeQuery = true)
    List<BookingHistoryTransaction> getByBookingId(@Param("id") String id);
}
