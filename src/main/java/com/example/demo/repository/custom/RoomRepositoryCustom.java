package com.example.demo.repository.custom;

import com.example.demo.dto.RoomRequestDTO;
import com.example.demo.dto.RoomResponeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {
    Page<RoomResponeDTO> search(RoomRequestDTO roomRequestDTO , Pageable pageable);


}
