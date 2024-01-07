package com.example.demo.repository;

import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query(value = "SELECT * FROM booking ORDER BY create_at DESC", nativeQuery = true)
    Page<Booking> findAll(Pageable pageable);

    @Query(value = "select * from booking b\n" +
            "where b.id_type_room =:typeRoomId\n" +
            "and b.check_in_date = :ci \n" +
            "and b.check_out_date =:co \n" +
            "and b.status =1 ", nativeQuery = true)
    List<Booking> checkRoomBooked(@Param("typeRoomId") String typeRoomId, @Param("ci") Date checkIn, @Param("co") Date checkOut);

    @Query(value = "select * from booking b\n" +
            "where b.id_type_room =:typeRoomId and b.status =1", nativeQuery = true)
    List<Booking> getAllByTypeRoom(@Param("typeRoomId") String typeRoomId);

}
