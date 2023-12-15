package com.example.demo.service;

import com.example.demo.dto.FavouriteRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavouriteService {
    boolean isLove(String idCustom, String idRoom);

    boolean setLove(String idCustom, String idRoom);

    Page<FavouriteRoomDTO> getFavouriteRoomsByCustomerId(String customerId, Pageable pageable);
}
