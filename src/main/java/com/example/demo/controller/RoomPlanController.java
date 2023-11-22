package com.example.demo.controller;

import com.example.demo.dto.RoomDTO;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/room")
public class RoomPlanController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/room-plan")
    public List<List<RoomDTO>> getRoomsByFloorsAscendingOrder() {
        List<List<RoomDTO>> roomDTOS = new ArrayList<>();
        List<List<Room>> list = roomService.getRoomsByAllFloors();
        for (List<Room> roomList : list) {
            List<RoomDTO> roomDTOList = new ArrayList<>();
            for (Room room : roomList) {
                RoomDTO roomDTO = new RoomDTO();
                roomDTO.setId(room.getId());
                roomDTO.setFloor(room.getFloor());
                roomDTO.setTypeRoom(room.getTypeRoom());
                roomDTO.setRoomCode(room.getRoomCode());
                roomDTO.setRoomName(room.getRoomName());
                roomDTO.setNote(room.getNote());
                roomDTO.setCreateAt(room.getCreateAt());
                roomDTO.setCreateBy(room.getCreateBy());
                roomDTO.setUpdateAt(room.getUpdateAt());
                roomDTO.setUpdatedBy(room.getUpdatedBy());
                roomDTO.setDeleted(room.getDeleted());
                List<String> roomImages = room.getPhotoList()
                        .stream()
                        .map(Photo::getUrl)
                        .collect(Collectors.toList());
                List<OrderDetail> orderDetailList = room.getOrderDetailList();
                roomDTO.setPhotoList(roomImages);
                roomDTO.setOrderDetailList(orderDetailList);
                roomDTO.setStatus(room.getStatus());
                roomDTOList.add(roomDTO);
            }
            roomDTOS.add(roomDTOList);
        }
//        Collections.reverse(roomsByAllFloors);
        return roomDTOS;
    }

}
