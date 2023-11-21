package com.example.demo.repository.custom;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomRepositoryCustom {
    Page<RoomResponeDTO> search(RoomRequestDTO roomRequestDTO, Pageable pageable);

    Page<RoomResponeDTO> topBook(Pageable pageable);

    List<CartDTO> getCart(String customId, Integer odStt);
}
