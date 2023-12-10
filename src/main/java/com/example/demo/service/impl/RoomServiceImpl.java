package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.RoomDTO;
import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import com.example.demo.entity.Floor;
import com.example.demo.entity.Room;
import com.example.demo.repository.FloorRepository;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private FloorRepository floorRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public List<Room> getAllByStatus(Integer status) {
        return roomRepository.findAllByStatus(status);
    }

    @Override
    public Page<Room> getAllByStatus(Integer status, Pageable pageable) {
        return roomRepository.findAllByStatus(status, pageable);
    }

    @Override
    public List<Room> getList() {
        return roomRepository.findAll();
    }

    @Override
    public Page<Room> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    public Page<Room> loadAndSearch(String roomCode, String roomName, String floorId, String typeRoomId, Pageable pageable) {
        return roomRepository.loadAndSearch(
                (roomCode != null && !roomCode.isEmpty()) ? roomCode : null,
                (roomName != null && !roomName.isEmpty()) ? "%" + roomName + "%" : null,
                (floorId != null && !floorId.isEmpty()) ? floorId : null,
                (typeRoomId != null && !typeRoomId.isEmpty()) ? typeRoomId : null,
                pageable
        );
    }

    @Override
    public Page<Room> loadAndSearchForHome(String roomCode, String roomName, String floorId, String typeRoomId, String id, Pageable pageable) {
        return roomRepository.loadAndSearchForHome(
                (roomCode != null && !roomCode.isEmpty()) ? roomCode : null,
                (roomName != null && !roomName.isEmpty()) ? "%" + roomName + "%" : null,
                (floorId != null && !floorId.isEmpty()) ? floorId : null,
                (typeRoomId != null && !typeRoomId.isEmpty()) ? typeRoomId : null,
                (id != null && !id.isEmpty()) ? id : null,
                pageable
        );
    }

    @Override
    public List<Room> findRoomsByFilters(
            String roomName,
            String typeRoomCode,
            BigDecimal startPrice,
            BigDecimal endPrice,
            Integer capacity,
            Date dayStart,
            Date dayEnd
    ) {
        // Check for null values and adjust the parameters accordingly
        String filteredRoomName = (roomName != null && !roomName.isEmpty()) ? roomName : null;
        String filteredTypeRoomCode = (typeRoomCode != null && !typeRoomCode.isEmpty()) ? typeRoomCode : null;
        BigDecimal filteredStartPrice = (startPrice != null) ? startPrice : BigDecimal.ZERO;
        BigDecimal filteredEndPrice = (endPrice != null) ? endPrice : BigDecimal.valueOf(Double.MAX_VALUE);
        Integer filteredCapacity = (capacity != null) ? capacity : 0;

        // Call the repository method with the filtered parameters
        return roomRepository.findRoomsByFilters(
                filteredRoomName,
                filteredTypeRoomCode,
                filteredStartPrice,
                filteredEndPrice,
                filteredCapacity,
                dayStart,
                dayEnd);
    }

    public List<Room> loadAndSearchBookRoom(String roomCode, String roomName, String floorId, String typeRoomId, BigDecimal start, BigDecimal end, Date dayStart, Date dayEnd) {
        return roomRepository.loadAndSearchBookRoom(
                (roomCode != null && !roomCode.isEmpty()) ? roomCode : null,
                (roomName != null && !roomName.isEmpty()) ? "%" + roomName + "%" : null,
                (floorId != null && !floorId.isEmpty()) ? floorId : null,
                (typeRoomId != null && !typeRoomId.isEmpty()) ? typeRoomId : null,
                start,
                end,
                dayStart,
                dayEnd
        );
    }

    @Override
    public List<Room> loadRoomByCondition(Integer capacity, Integer adult, Integer children, Date dayStart, Date dayEnd) {
        return roomRepository.loadRoomByCondition(capacity, adult, children, dayStart, dayEnd);
    }

    @Override
    public Page<Room> findRoomsOrderByOrderDetailCountDesc(Pageable pageable) {
        return roomRepository.findRoomsOrderByOrderDetailCountDesc(pageable);
    }


    @Override
    public Room getRoomById(String id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public Room add(Room room) {
        try {
            return roomRepository.save(room);
        } catch (Exception e) {
            System.out.println("Add error!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            roomRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public boolean existsByRoomCode(String code) {
        return roomRepository.existsByRoomCode(code);
    }

    @Override
    public boolean existsByRoomName(String name) {
        return roomRepository.existsByRoomName(name);
    }

    @Override
    public Page<RoomResponeDTO> search(RoomRequestDTO roomRequestDTO, Pageable pageable) {
        Page<RoomResponeDTO> page = roomRepository.search(roomRequestDTO, pageable);
        for (RoomResponeDTO roomResponeDTO1 : page.getContent()
        ) {
            List<String> url = photoRepository.getUrlByIdRoom(roomResponeDTO1.getId());
            roomResponeDTO1.setUrls(url);
        }
        return page;
    }

    @Override
    public Page<RoomResponeDTO> topBook(Pageable pageable) {
        Page<RoomResponeDTO> page = roomRepository.topBook(pageable);
        for (RoomResponeDTO roomResponeDTO1 : page.getContent()
        ) {
            List<String> url = photoRepository.getUrlByIdRoom(roomResponeDTO1.getId());
            roomResponeDTO1.setUrls(url);
        }
        return page;
    }

    @Override
    public List<List<Room>> getRoomsByAllFloors() {
        List<Floor> allFloors = floorRepository.findAll();
        List<List<Room>> roomsByAllFloors = new ArrayList<>();

        for (Floor floor : allFloors) {
            List<Room> roomsInFloor = roomRepository.getRoomsByFloorId(floor.getId());
            roomsByAllFloors.add(roomsInFloor);
        }

        return roomsByAllFloors;
    }

    @Override
    public List<CartDTO> getCart(String customId, Integer odStt) {
        List<CartDTO> cartDTOS = new ArrayList<>();
        if (odStt == 7) {
            // lọc ra hóa đơn hết hạn phê duyệt
            for (CartDTO cartDTO : roomRepository.getCart(customId, Constant.ORDER_DETAIL.WAIT_CONFIRM)) {
                Date dateCheckIn = cartDTO.getBookingStart();
                if (dateCheckIn != null && dateCheckIn.before(new Date())) {
                    cartDTOS.add(cartDTO);
                }
            }
        } else if (odStt.equals(Constant.ORDER_DETAIL.WAIT_CONFIRM)) {
            for (CartDTO cartDTO : roomRepository.getCart(customId, Constant.ORDER_DETAIL.WAIT_CONFIRM)) {
                Date dateCheckIn = cartDTO.getBookingEnd();
                if (dateCheckIn != null && dateCheckIn.after(new Date())) {
                    cartDTOS.add(cartDTO);
                }
            }
        } else {
            cartDTOS = roomRepository.getCart(customId, odStt);
        }
        return cartDTOS;
    }

    @Override
    public List<Room> getTopRoom() {
        return roomRepository.getTopRoom();
    }

}
