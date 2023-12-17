package com.example.demo.repository.custom;

import com.example.demo.dto.*;
import com.example.demo.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomRepositoryCustom {
    Page<RoomResponeDTO> search(RoomRequestDTO roomRequestDTO, Pageable pageable);

    Page<RoomResponeDTO> topBook(Pageable pageable);

    List<CartDTO> getCart(String customId, Integer odStt);

    Page<RoomCardDTO> searchRoom(FacilityRequestDTO facilityRequestDTO, Pageable pageable);
}
