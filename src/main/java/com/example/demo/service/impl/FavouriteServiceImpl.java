package com.example.demo.service.impl;

import com.example.demo.entity.Favourite;
import com.example.demo.repository.FavouriteRepository;
import com.example.demo.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public boolean setLove(String idCustom, String idRoom) {
        List<Favourite> list = favouriteRepository.findByIdCustomAndIdRoom(idCustom, idRoom);
        if (list.size() != 0) {
            Favourite favourite1 = list.get(0);
            favouriteRepository.delete(favourite1);
            return false;
        }

        Favourite favourite = new Favourite();
        favourite.setCustomer(idCustom);
        favourite.setRoom(idRoom);
        favourite.setStatus(1);
        try {
            favouriteRepository.save(favourite);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
