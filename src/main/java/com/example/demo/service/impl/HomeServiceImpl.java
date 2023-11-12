package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.Message;
import com.example.demo.entity.Order;
import com.example.demo.service.HomeService;
import com.example.demo.service.OrderService;
import com.example.demo.util.DataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final OrderService orderService;

    @Override
    public Message cancelOrder(String code, Integer oddStt) {
        // kiểm tra tồn tại
        Order order;
        if (code.length() == 36) {
            order = orderService.getOrderById(code);
        } else {
            order = orderService.getOrderByCode(code);
        }

        if (DataUtil.isNull(order)) {
            return new Message(Constant.MESSAGE_STATUS.ERROR, "Hóa đơn không tồn tại !");
        }
        if (oddStt.equals(Constant.ORDER_STATUS.REFUSE)) {
            orderService.refuse(order.getId(), oddStt);
        }
        if (oddStt.equals(Constant.ORDER_STATUS.CANCEL)) {
            orderService.cancel(order.getId(), oddStt);
        }
        return new Message(Constant.MESSAGE_STATUS.SUCCESS, "Thành công !");
    }
}
