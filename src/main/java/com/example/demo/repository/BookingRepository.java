package com.example.demo.repository;

import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query(value = "SELECT * FROM booking ORDER BY create_at DESC", nativeQuery = true)
    Page<Booking> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM booking WHERE id_order = ?1", nativeQuery = true)
    Booking getByIdOrder(String idOrder);

}
