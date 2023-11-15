package com.example.demo.repository;

import com.example.demo.dto.RoomDTO;
import com.example.demo.entity.Room;
import com.example.demo.repository.custom.RoomRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String>, RoomRepositoryCustom {

    @Query(value = "select * from room order by update_at DESC", nativeQuery = true)
    Page<Room> findAll(Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM room\n" +
            "  WHERE ((:roomCode IS NULL OR room_code = :roomCode)\n" +
            "       OR (:roomName IS NULL OR room_name LIKE CONCAT('%', :roomName, '%')))\n" +
            "  AND (:floorId IS NULL OR floor_id = :floorId)\n" +
            "  AND (:typeRoomId IS NULL OR type_room_id = :typeRoomId) " +
            " AND status =1" +
            " ORDER BY update_at DESC", nativeQuery = true)
    Page<Room> loadAndSearch(@Param("roomCode") String roomCode,
                             @Param("roomName") String roomName,
                             @Param("floorId") String floorId,
                             @Param("typeRoomId") String typeRoomId,
                             Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM room\n" +
            "WHERE status = 1\n" +
            "  AND ((:roomCode IS NULL OR room_code = :roomCode)\n" +
            "       OR (:roomName IS NULL OR room_name LIKE CONCAT('%', :roomName, '%')))\n" +
            "  AND (:floorId IS NULL OR floor_id = :floorId)\n" +
            "  AND (:typeRoomId IS NULL OR type_room_id = :typeRoomId) " +
            "AND id <> :id" +
            " ORDER BY update_at DESC", nativeQuery = true)
    Page<Room> loadAndSearchForHome(@Param("roomCode") String roomCode,
                                    @Param("roomName") String roomName,
                                    @Param("floorId") String floorId,
                                    @Param("typeRoomId") String typeRoomId,
                                    @Param("id") String id,
                                    Pageable pageable);

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN r.orderDetailList  od " +
            "LEFT JOIN od.order o " +
            "LEFT JOIN r.typeRoom tr " +
            "WHERE r.roomName LIKE CONCAT('%', :roomName, '%') " +
            "AND (:typeRoomCode IS NULL OR tr.typeRoomCode = :typeRoomCode) " +
            "AND tr.pricePerDay BETWEEN :startPrice AND :endPrice " +
            "AND (:capacity IS NULL OR tr.capacity = :capacity) " +
            "AND ((" +
            "    (o.bookingDateStart NOT BETWEEN :dayStart AND :dayEnd) " +
            "    AND (o.bookingDateEnd NOT BETWEEN :dayStart AND :dayEnd)) " +
            "    OR (:dayStart IS NULL OR :dayEnd IS NULL))")
    List<Room> findRoomsByFilters(
            @Param("roomName") String roomName,
            @Param("typeRoomCode") String typeRoomCode,
            @Param("startPrice") BigDecimal startPrice,
            @Param("endPrice") BigDecimal endPrice,
            @Param("capacity") Integer capacity,
            @Param("dayStart") Date dayStart,
            @Param("dayEnd") Date dayEnd
    );

    @Query("SELECT r FROM Room r " +
            "LEFT JOIN FETCH r.orderDetailList od " +
            "GROUP BY r.id " +
            "ORDER BY COUNT(od) DESC")
    Page<Room> findRoomsOrderByOrderDetailCountDesc(Pageable pageable);

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN r.orderDetailList od " +
            "LEFT JOIN r.floor fl " +
            "LEFT JOIN r.typeRoom tr " +
            "WHERE ((:roomCode IS NULL OR r.roomCode =: roomCode)" +
            " OR (:roomName IS NULL OR r.roomName LIKE CONCAT('%', :roomName, '%')))" +
            "AND (:typeRoomId IS NULL OR tr.id = :typeRoomId) " +
            "AND (:floorId IS NULL OR fl.id = :floorId) " +
            "AND tr.pricePerDay BETWEEN :start AND :end " +
            "AND ((od.checkInDatetime NOT BETWEEN :dayStart AND :dayEnd) " +
            "    AND (od.checkOutDatetime NOT BETWEEN :dayStart AND :dayEnd)" +
            "    OR (:dayStart IS NULL OR :dayEnd IS NULL))" +
            "AND r.status IN (1, 2) ORDER BY r.updateAt DESC")
    List<Room> loadAndSearchBookRoom(@Param("roomCode") String roomCode,
                                     @Param("roomName") String roomName,
                                     @Param("floorId") String floorId,
                                     @Param("typeRoomId") String typeRoomId,
                                     @Param("start") BigDecimal start,
                                     @Param("end") BigDecimal end,
                                     @Param("dayStart") Date dayStart,
                                     @Param("dayEnd") Date dayEnd);

//    @Query(value = "SELECT DISTINCT r.id, r.floor_id, r.type_room_id, r.room_code, r.room_name, r.note, r.create_at, r.create_by," +
//            " r.update_at, r.updated_by, r.deleted, r.status, tr.price_per_day \n" +
//            "FROM room r JOIN type_room tr ON r.type_room_id = tr.id\n" +
//            "LEFT JOIN order_detail od ON r.id = od.room_id" +
//            "  WHERE ((:roomCode IS NULL OR r.room_code = :roomCode)\n" +
//            "       OR (:roomName IS NULL OR r.room_name LIKE CONCAT('%', :roomName, '%')))\n" +
//            "  AND (:floorId IS NULL OR r.floor_id = :floorId)\n" +
//            "  AND (:typeRoomId IS NULL OR r.type_room_id = :typeRoomId)" +
//            "  AND tr.price_per_day BETWEEN :start AND :end\n" +
//            "AND ((" +
//            "    (od.check_in_datetime NOT BETWEEN :dayStart AND :dayEnd) " +
//            "    AND (od.check_out_datetime NOT BETWEEN :dayStart AND :dayEnd)) " +
//            "    OR (:dayStart IS NULL OR :dayEnd IS NULL))" +
//            "ORDER BY update_at DESC", nativeQuery = true)
//    List<Room> loadAndSearchBookRoom(@Param("roomCode") String roomCode,
//                                     @Param("roomName") String roomName,
//                                     @Param("floorId") String floorId,
//                                     @Param("typeRoomId") String typeRoomId,
//                                     @Param("start") BigDecimal start,
//                                     @Param("end") BigDecimal end,
//                                     @Param("dayStart") Date dayStart,
//                                     @Param("dayEnd") Date dayEnd);

    boolean existsByRoomCode(String code);

    boolean existsByRoomName(String name);

    @Query(value = "select * from room where  status = :status order by update_at  DESC", nativeQuery = true)
    List<Room> findAllByStatus(@Param("status") Integer status);

    @Query(value = "select * from room where  status = :status order by update_at  DESC", nativeQuery = true)
    Page<Room> findAllByStatus(@Param("status") Integer status, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.floor.id = :floorId")
    List<Room> getRoomsByFloorId(String floorId);

    @Query(value = "SELECT r FROM Room r LEFT JOIN r.photoList p" +
            " LEFT JOIN r.orderDetailList od GROUP BY r.id, r.roomName, p.url" +
            " ORDER BY COUNT(od.room.id) DESC")
    List<Room> getTopRoom();

}
