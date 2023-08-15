package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(value = "select * from room order by create_at DESC", nativeQuery = true)
    Page<Room> findAll(Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM room\n" +
            "  WHERE ((:roomCode IS NULL OR room_code = :roomCode)\n" +
            "       OR (:roomName IS NULL OR room_name LIKE CONCAT('%', :roomName, '%')))\n" +
            "  AND (:floorId IS NULL OR floor_id = :floorId)\n" +
            "  AND (:typeRoomId IS NULL OR type_room_id = :typeRoomId) " +
            " AND status =1" +
            " ORDER BY create_at DESC", nativeQuery = true)
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
            " ORDER BY create_at DESC", nativeQuery = true)
    Page<Room> loadAndSearchForHome(@Param("roomCode") String roomCode,
                                    @Param("roomName") String roomName,
                                    @Param("floorId") String floorId,
                                    @Param("typeRoomId") String typeRoomId,
                                    @Param("id") String id,
                                    Pageable pageable);

    @Query(value = "SELECT r.id, r.floor_id, r.type_room_id, r.room_code, r.room_name, r.note, r.create_at, r.create_by," +
            " r.update_at, r.updated_by, r.deleted, r.status, tr.price_per_day \n" +
            "FROM room r JOIN type_room tr ON r.type_room_id = tr.id\n" +
            "  WHERE ((:roomCode IS NULL OR r.room_code = :roomCode)\n" +
            "       OR (:roomName IS NULL OR r.room_name LIKE CONCAT('%', :roomName, '%')))\n" +
            "  AND (:floorId IS NULL OR r.floor_id = :floorId)\n" +
            "  AND (:typeRoomId IS NULL OR r.type_room_id = :typeRoomId)" +
            "  AND tr.price_per_day BETWEEN :start AND :end\n" +
            "ORDER BY create_at DESC", nativeQuery = true)
    List<Room> loadAndSearchBookRoom(@Param("roomCode") String roomCode,
                                     @Param("roomName") String roomName,
                                     @Param("floorId") String floorId,
                                     @Param("typeRoomId") String typeRoomId,
                                     @Param("start") BigDecimal start,
                                     @Param("end") BigDecimal end);

    boolean existsByRoomCode(String code);

    boolean existsByRoomName(String name);

    @Query(value = "select * from room order by create_at DESC", nativeQuery = true)
    List<Room> findAllByStatus();

}
