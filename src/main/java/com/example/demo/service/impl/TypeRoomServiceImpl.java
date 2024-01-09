package com.example.demo.service.impl;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Room;
import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.TypeRoomRepository;
import com.example.demo.service.BookingService;
import com.example.demo.service.RoomService;
import com.example.demo.service.TypeRoomService;
import com.example.demo.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TypeRoomServiceImpl implements TypeRoomService {

    @Autowired
    private TypeRoomRepository typeRoomRepository;
    @Autowired
    private RoomRepository romRoomRepository;
    @Autowired
    private BookingRepository bookingRepository;


    @Override
    public List<TypeRoom> getList() {
        return typeRoomRepository.findAll();
    }

    @Override
    public Page<TypeRoom> getAll(Pageable pageable) {
        return typeRoomRepository.findAll(pageable);
    }

    @Override
    public Page<TypeRoom> findByCodeOrName(String key, Pageable pageable) {
        return typeRoomRepository.findByCodeOrName(key, "%" + key + "%", pageable);
    }

    @Override
    public TypeRoom getTypeRoomById(String id) {
        return typeRoomRepository.findById(id).orElse(null);
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

    @Override
    public Integer countRoomCanBeBook(String typeRoomName, Date checkIn, Date checkOut) {
        List<TypeRoom> typeRoom = typeRoomRepository.findByName(typeRoomName);
        if(typeRoom.size()==0) return 0;
        // B1 : lấy ra số lượng phòng của loại phòng
        List<Room> list = romRoomRepository.findByTypeRoomId(typeRoom.get(0).getId());
        if (list.size() == 0) return 0;
        // B2 : Lấy tất cả các đơn booking với type =1 ( Thanh toán thành công ) của loại phòng
        List<Booking> bookingList = bookingRepository.getAllByTypeRoom(typeRoom.get(0).getId());
        // B3 : Kiểm tra ngày check in check out có nằm trong khoảng ngày đã đặt
        List<Booking> bookingNotOk = new ArrayList<>();
        for (Booking booking : bookingList) {
            // Nếu check in > checkinDb -> check out > CheckOutDb
            // Nếu check in < checkinDb -> check out < CheckOutDb
            if (!(booking.getCheckInDate().before(checkIn) && booking.getCheckOutDate().before(checkOut)) || !(booking.getCheckInDate().after(checkIn) && booking.getCheckOutDate().after(checkOut))) {
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
