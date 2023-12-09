package com.example.demo.service;

import com.example.demo.dto.ConfirmOrderDTO;
import com.example.demo.dto.RevenueDTO;
import com.example.demo.entity.Order;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderService {

    List<Order> getList();

    Page<Order> getAll(Pageable pageable);

    Page<Order> getAllByStatus(Pageable pageable);

    Page<Order> loadAndSearch(String orderCode, Boolean typeOfOrder, Integer status, String customerFullname, Date startDate, Date endDate, Pageable pageable);

    Page<Order> loadBookRoomOnline(String orderCode, String customerFullname, String customerPhone, String customerEmail, Integer status, Pageable pageable);

    Page<Order> loadBookRoomOffline(String orderCode, Integer status, Pageable pageable);

    List<Order> loadNotify();

    Order getOrderById(String id);

    Order getOrderByCode(String code);

    Long countOrderCancel();

    Long countOrderWait();

    Long countOrderConfirm();

    Long countOrderAccept();

    Order add(Order order);

    void delete(String id);

    ConfirmOrderDTO confirmOrder(ConfirmOrderDTO confirmOrderDTO);

    ConfirmOrderDTO updateSurcharge(ConfirmOrderDTO confirmOrderDTO);

    BigDecimal getRevenueMonth();

    BigDecimal getRevenueYear();

    ByteArrayResource exportRecommended(String order) throws JRException;

    List<RevenueDTO> getRevenue();

    void refuse(String id, Integer stt, String refuseReason);

    void cancel(String id, Integer stt);

}
