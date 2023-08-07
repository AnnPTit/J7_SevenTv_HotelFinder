package com.example.demo.repository;

import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(value = "select * from room where status = 1 order by create_at DESC", nativeQuery = true)
    Page<Room> findAll(Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM room\n" +
            "WHERE status = 1\n" +
            "  AND ((:roomCode IS NULL OR room_code = :roomCode)\n" +
            "       OR (:roomName IS NULL OR room_name LIKE CONCAT('%', :roomName, '%')))\n" +
            "  AND (:floorId IS NULL OR floor_id = :floorId)\n" +
            "  AND (:typeRoomId IS NULL OR type_room_id = :typeRoomId) ORDER BY create_at DESC", nativeQuery = true)
    Page<Room> loadAndSearch(@Param("roomCode") String roomCode,
                             @Param("roomName") String roomName,
                             @Param("floorId") String floorId,
                             @Param("typeRoomId") String typeRoomId,
                             Pageable pageable);

    boolean existsByRoomCode(String code);

    boolean existsByRoomName(String name);

    @Query(value = "select * from room where status = 1 order by create_at DESC", nativeQuery = true)
    List<Room> findAllByStatus();

}
