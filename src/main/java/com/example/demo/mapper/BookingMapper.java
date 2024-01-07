package com.example.demo.mapper;

import com.example.demo.dto.BookingDTO;
import com.example.demo.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public static BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setIdTypeRoom(booking.getIdTypeRoom());
        dto.setIdCustomer(booking.getIdCustomer());
        dto.setIdOrder(booking.getIdOrder());
        dto.setNote(booking.getNote());
        dto.setNumberRooms(booking.getNumberRooms());
        dto.setNumberCustomers(booking.getNumberCustomers());
        dto.setNumberAdults(booking.getNumberAdults());
        dto.setNumberChildren(booking.getNumberChildren());
        dto.setNumberDays(booking.getNumberDays());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setRoomPrice(booking.getRoomPrice());
        dto.setVat(booking.getVat());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBankAccountNumber(booking.getBankAccountNumber());
        dto.setBankAccountName(booking.getBankAccountName());
        dto.setCreateAt(booking.getCreateAt());
        dto.setCreateBy(booking.getCreateBy());
        dto.setUpdateAt(booking.getUpdateAt());
        dto.setUpdatedBy(booking.getUpdatedBy());
        dto.setDeleted(booking.getDeleted());
        dto.setStatus(booking.getStatus());

        return dto;
    }

    public static Booking toEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setIdTypeRoom(dto.getIdTypeRoom());
        booking.setIdCustomer(dto.getIdCustomer());
        booking.setIdOrder(dto.getIdOrder());
        booking.setNote(dto.getNote());
        booking.setNumberRooms(dto.getNumberRooms());
        booking.setNumberCustomers(dto.getNumberCustomers());
        booking.setNumberAdults(dto.getNumberAdults());
        booking.setNumberChildren(dto.getNumberChildren());
        booking.setNumberDays(dto.getNumberDays());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setRoomPrice(dto.getRoomPrice());
        booking.setVat(dto.getVat());
        booking.setTotalPrice(dto.getTotalPrice());
        booking.setBankAccountNumber(dto.getBankAccountNumber());
        booking.setBankAccountName(dto.getBankAccountName());
        booking.setCreateAt(dto.getCreateAt());
        booking.setCreateBy(dto.getCreateBy());
        booking.setUpdateAt(dto.getUpdateAt());
        booking.setUpdatedBy(dto.getUpdatedBy());
        booking.setDeleted(dto.getDeleted());
        booking.setStatus(dto.getStatus());

        return booking;
    }
}
