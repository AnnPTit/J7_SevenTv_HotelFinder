package com.example.demo.service;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.FacilityRequestDTO;
import com.example.demo.dto.RoomCardDTO;
import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RoomService {

    List<Room> getAllByStatus(Integer status);

    Page<Room> getAllByStatus(Integer status, Pageable pageable);

    List<Room> getList();

    Page<Room> getAll(Pageable pageable);

    Page<Room> loadAndSearch(String roomCode, String roomName, String floorId, String typeRoomId, Integer status, Pageable pageable);

    Page<Room> loadAndSearchForHome(String roomCode, String roomName, String floorId, String typeRoomId, String id, Pageable pageable);

    List<Room> findRoomsByFilters(
            String roomName, String typeRoomCode,
            BigDecimal startPrice,
            BigDecimal endPrice,
            Integer capacity,
            Date dayStart,
            Date dayEnd
    );

    Page<Room> findRoomsOrderByOrderDetailCountDesc(Pageable pageable);

    List<Room> loadAndSearchBookRoom(String roomCode, String roomName, String floorId, String typeRoomId, BigDecimal start, BigDecimal end, Date dayStart, Date dayEnd);

    List<Room> loadRoomByCondition(String typeRoomId, Integer capacity, Integer adult, Integer children, Date dayStart, Date dayEnd);

    Room getRoomById(String id);

    Room add(Room room);

    void delete(String id);

    boolean existsByRoomCode(String code);

    boolean existsByRoomName(String name);

    Page<RoomResponeDTO> search(RoomRequestDTO roomRequestDTO, Pageable pageable);

    Page<RoomResponeDTO> topBook(Pageable pageable);

    List<List<Room>> getRoomsByAllFloors(Integer status, String roomCode, String roomName, String floorId, String typeRoomId);

    List<CartDTO> getCart(String customId, Integer odStt);

    List<Room> getTopRoom();

    Page<RoomCardDTO> searchRoom(FacilityRequestDTO facilityRequestDTO, Pageable pageable);

}
