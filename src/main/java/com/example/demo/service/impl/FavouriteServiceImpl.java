package com.example.demo.service.impl;

import com.example.demo.repository.FavouriteRepository;
import com.example.demo.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {
    private final FavouriteRepository favouriteRepository;

    @Override
    public boolean isLove(String idCustom, String idRoom) {
        if (favouriteRepository.findByIdCustomAndIdRoom(idCustom, idRoom).size() == 0) {
            return false;
        }
        return true;
    }
}
