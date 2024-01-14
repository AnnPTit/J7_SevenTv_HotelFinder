package com.example.demo.repository;

import com.example.demo.entity.Booking;
import com.example.demo.repository.custom.BookingRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> , BookingRepositoryCustom {

    @Query(value = "SELECT o.* FROM booking o " +
            "JOIN customer c ON o.id_customer = c.id " +
            "WHERE ((:customerFullname IS NULL OR c.fullname LIKE CONCAT('%', :customerFullname, '%'))" +
            "OR (:customerPhone IS NULL OR c.phone_number LIKE CONCAT('%', :customerPhone, '%'))" +
            "OR (:customerEmail IS NULL OR c.email LIKE CONCAT('%', :customerEmail, '%'))) " +
            " AND (:status IS NULL OR o.status = :status) ORDER BY o.update_at DESC", nativeQuery = true)
    Page<Booking> findAll(@Param("customerFullname") String customerFullname,
                          @Param("customerPhone") String customerPhone,
                          @Param("customerEmail") String customerEmail,
                          @Param("status") Integer status, Pageable pageable);

    @Query(value = "SELECT * FROM booking WHERE id_order = ?1", nativeQuery = true)
    Booking getByIdOrder(String idOrder);

    @Query(value = "select * from booking b\n" +
            "where b.id_type_room =:typeRoomId\n" +
            "and b.check_in_date = :ci \n" +
            "and b.check_out_date =:co \n" +
            "and b.status =1 ", nativeQuery = true)
    List<Booking> checkRoomBooked(@Param("typeRoomId") String typeRoomId, @Param("ci") Date checkIn, @Param("co") Date checkOut);

    @Query(value = "select * from booking b\n" +
            "where b.id_type_room =:typeRoomId and b.status in (1,2 )", nativeQuery = true)
    List<Booking> getAllByTypeRoom(@Param("typeRoomId") String typeRoomId);

    @Query(value = "select * from booking b\n" +
            "where b.status =:status  and b.id_customer =:idCuss", nativeQuery = true)
    List<Booking> getAllByStatus(@Param("status") Integer status, @Param("idCuss") String idCuss);


    @Modifying
    @Query(value = "update booking set status = 6 where id = :id ", nativeQuery = true)
    void updateCancel(@Param("id") String id);
}
