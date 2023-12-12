package com.example.demo.service.impl;

import com.example.demo.dto.FavouriteRoomDTO;
import com.example.demo.entity.Favourite;
import com.example.demo.entity.Room;
import com.example.demo.repository.FavouriteRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.FavouriteService;
import com.example.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {
    private final FavouriteRepository favouriteRepository;

    @Autowired
    private RoomRepository roomRepository;

    private final PhotoService photoService;
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

    @Override
    public Page<FavouriteRoomDTO> getFavouriteRoomsByCustomerId(String customerId, Pageable pageable) {
        Page<Favourite> favourites = favouriteRepository.findByCustomer(customerId, pageable);

        return favourites.map(favourite -> {
            Room room = roomRepository.findById(favourite.getRoom()).orElse(null);
            if (room != null) {
                return FavouriteRoomDTO.builder()
                        .roomId(room.getId())
                        .roomName(room.getRoomName())
                        .floor(room.getFloor().getFloorName())
                        .typeRoom(room.getTypeRoom().getTypeRoomName())
                        .typeRoomNote(room.getTypeRoom().getNote())
                        .capacity(room.getTypeRoom().getCapacity())
                        .pricePerHours(room.getTypeRoom().getPricePerHours())
                        .pricePerDay(room.getTypeRoom().getPricePerDay())
                        .photoList(photoService.getUrlByIdRoom(room.getId()))
                        .roomFacilityList(room.getRoomFacilityList())
                        .build();
            }
            return null;
        });
    }

}
