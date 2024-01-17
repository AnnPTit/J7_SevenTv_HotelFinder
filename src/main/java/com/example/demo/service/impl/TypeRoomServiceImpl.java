package com.example.demo.service.impl;

import com.example.demo.dto.TypeRoomDTO;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Room;
import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.TypeRoomRepository;
import com.example.demo.service.PhotoService;
import com.example.demo.service.TypeRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeRoomServiceImpl implements TypeRoomService {

    @Autowired
    private TypeRoomRepository typeRoomRepository;
    @Autowired
    private RoomRepository romRoomRepository;
    @Autowired
    private BookingRepository bookingRepository;


    private final PhotoService photoService;

    @Override
    public List<TypeRoom> getList() {
        return typeRoomRepository.findAll();
    }

    @Override
    public Page<TypeRoom> getAll(Pageable pageable) {
        return typeRoomRepository.findAll(pageable);
    }

    @Override
    public Page<TypeRoomDTO> findByCodeOrName(String key, Pageable pageable) {
        return typeRoomRepository.findByCodeOrName(key, "%" + key + "%", pageable).map(item -> toDTO(item));
    }

    @Override
    public TypeRoom getTypeRoomById(String id) {
        return typeRoomRepository.findById(id).orElse(null);
    }

    @Override
    public TypeRoom getTypeRoomByRoomId(String roomId) {
        if (roomId == null) {
            return null;
        }
        List<TypeRoom> list = typeRoomRepository.getTypeRoomByRoomId(roomId);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TypeRoom findByName(String name) {
        List<TypeRoom> list = typeRoomRepository.findByName(name);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TypeRoom add(TypeRoom typeRoom) {
        try {
            return typeRoomRepository.save(typeRoom);
        } catch (Exception e) {
            System.out.println("Add Error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            typeRoomRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete Error!");
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return typeRoomRepository.existsByTypeRoomCode(code);
    }

    public TypeRoomDTO toDTO(TypeRoom entity) {
        TypeRoomDTO typeRoomDTO = new TypeRoomDTO();
        typeRoomDTO.setId(entity.getId());
        typeRoomDTO.setTypeRoomCode(entity.getTypeRoomCode());
        typeRoomDTO.setTypeRoomName(entity.getTypeRoomName());
        typeRoomDTO.setPricePerDay(entity.getPricePerDay());
        typeRoomDTO.setPricePerHours(entity.getPricePerHours());
        typeRoomDTO.setCapacity(entity.getCapacity());
        typeRoomDTO.setAdult(entity.getAdult());
        typeRoomDTO.setChildren(entity.getChildren());
        typeRoomDTO.setNote(entity.getNote());
        typeRoomDTO.setCreateAt(entity.getCreateAt());
        typeRoomDTO.setCreateBy(entity.getCreateBy());
        typeRoomDTO.setUpdateAt(entity.getUpdateAt());
        typeRoomDTO.setUpdatedBy(entity.getUpdatedBy());
        typeRoomDTO.setStatus(entity.getStatus());
        List<String> photo = photoService.getUrlByIdTypeRoom(entity.getId());
        if (photo.size() != 0) {
            typeRoomDTO.setPhotoDTOS(photo);
        }
        return typeRoomDTO;
    }

    @Override
    public Integer countRoomCanBeBook(String typeRoomName, Date checkIn, Date checkOut) {
        List<TypeRoom> typeRoom = typeRoomRepository.findByName(typeRoomName);
        if (typeRoom.size() == 0) return 0;
        // B1 : lấy ra số lượng phòng của loại phòng
        List<Room> list = romRoomRepository.findByTypeRoomId(typeRoom.get(0).getId());
        if (list.size() == 0) return 0;
        // B2 : Lấy tất cả các đơn booking với type = 1 ( Thanh toán thành công ) của loại phòng
        List<Booking> bookingList = bookingRepository.getAllByTypeRoom(typeRoom.get(0).getId());
        // B3 : Kiểm tra ngày check in check out có nằm trong khoảng ngày đã đặt
        List<Booking> bookingNotOk = new ArrayList<>();
        for (Booking booking : bookingList) {
            Date bookingDbCi = booking.getCheckInDate();
            Date bookingDbCo = booking.getCheckOutDate();
            // Checkin và check out không nằm giữa khoảng check in check out trong đb
            if ((!bookingDbCi.after(checkIn) && bookingDbCo.after(checkIn)) || (!bookingDbCi.after(checkOut) && bookingDbCo.after(checkOut))) {
                bookingNotOk.add(booking);
            }
        }
        // bookingNotOk sẽ là list chứa các booking không thỏa mãn và sẽ phải trừ số lượng đi
        if (bookingNotOk.size() == 0) return list.size();
        Integer numberRoomNotOk = bookingNotOk.stream()
                .mapToInt(Booking::getNumberRooms)
                .sum();
        // B4 : lấy tổng số phòng trừ đi số phòng không hợp lệ -> số phòng có thể đặt
        Integer numberRoomOk = list.size() - numberRoomNotOk;
        return numberRoomOk;
    }

    @Override
    public Integer countRoomCanBeBook2(String typeRoomName, Date checkIn, Date checkOut) {
        List<TypeRoom> typeRoom = typeRoomRepository.findByName(typeRoomName);
        if (typeRoom.size() == 0) return 0;
        // B1 : lấy ra số lượng phòng của loại phòng
        List<Room> list = romRoomRepository.findByTypeRoomId(typeRoom.get(0).getId());
        if (list.size() == 0) return 0;
        // B2 : Lấy tất cả các đơn booking với type = 1 ( Thanh toán thành công ) của loại phòng
        List<Booking> bookingList = bookingRepository.getAllByTypeRoom(typeRoom.get(0).getId());
        // B3 : Kiểm tra ngày check in check out có nằm trong khoảng ngày đã đặt
        List<Booking> bookingNotOk = new ArrayList<>();
        for (Booking booking : bookingList) {
            Date bookingDbCi = booking.getCheckInDate();
            Date bookingDbCo = booking.getCheckOutDate();
            // Checkin và check out không nằm giữa khoảng check in check out trong đb
            if ((!checkIn.after(bookingDbCi) && !bookingDbCi.after(checkOut)) || (!checkIn.after(bookingDbCo) && !bookingDbCo.after(checkOut)) ) {
                bookingNotOk.add(booking);
            }
        }
        // bookingNotOk sẽ là list chứa các booking không thỏa mãn và sẽ phải trừ số lượng đi
        if (bookingNotOk.size() == 0) return list.size();
        Integer numberRoomNotOk = bookingNotOk.stream()
                .mapToInt(Booking::getNumberRooms)
                .sum();
        // B4 : lấy tổng số phòng trừ đi số phòng không hợp lệ -> số phòng có thể đặt
        Integer numberRoomOk = list.size() - numberRoomNotOk;
        return numberRoomOk;
    }
}
