package com.example.demo.mapper;

import com.example.demo.dto.BookingHistoryTransactionDTO;
import com.example.demo.entity.BookingHistoryTransaction;

public class BookingHistoryTransactionMapper {
    public static BookingHistoryTransactionDTO toDTO(BookingHistoryTransaction historyTransaction) {
        BookingHistoryTransactionDTO dto = new BookingHistoryTransactionDTO();
        dto.setId(historyTransaction.getId());
        dto.setIdBooking(historyTransaction.getIdBooking());
        dto.setTotalPrice(historyTransaction.getTotalPrice());
        dto.setPaymentMethod(historyTransaction.getPaymentMethod());
        dto.setType(historyTransaction.getType());
        dto.setNote(historyTransaction.getNote());
        dto.setCancelReason(historyTransaction.getCancelReason());
        dto.setCancelDate(historyTransaction.getCancelDate());
        dto.setRefundMoney(historyTransaction.getRefundMoney());
        dto.setCreateAt(historyTransaction.getCreateAt());
        dto.setCreateBy(historyTransaction.getCreateBy());
        dto.setUpdateAt(historyTransaction.getUpdateAt());
        dto.setUpdatedBy(historyTransaction.getUpdatedBy());
        dto.setDeleted(historyTransaction.getDeleted());
        dto.setStatus(historyTransaction.getStatus());

        return dto;
    }

    public static BookingHistoryTransaction toEntity(BookingHistoryTransactionDTO dto) {
        BookingHistoryTransaction historyTransaction = new BookingHistoryTransaction();
        historyTransaction.setId(dto.getId());
        historyTransaction.setIdBooking(dto.getIdBooking());
        historyTransaction.setTotalPrice(dto.getTotalPrice());
        historyTransaction.setPaymentMethod(dto.getPaymentMethod());
        historyTransaction.setType(dto.getType());
        historyTransaction.setNote(dto.getNote());
        historyTransaction.setCancelReason(dto.getCancelReason());
        historyTransaction.setCancelDate(dto.getCancelDate());
        historyTransaction.setRefundMoney(dto.getRefundMoney());
        historyTransaction.setCreateAt(dto.getCreateAt());
        historyTransaction.setCreateBy(dto.getCreateBy());
        historyTransaction.setUpdateAt(dto.getUpdateAt());
        historyTransaction.setUpdatedBy(dto.getUpdatedBy());
        historyTransaction.setDeleted(dto.getDeleted());
        historyTransaction.setStatus(dto.getStatus());

        return historyTransaction;
    }
}
