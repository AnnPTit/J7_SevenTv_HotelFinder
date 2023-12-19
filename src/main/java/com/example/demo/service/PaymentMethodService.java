package com.example.demo.service;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface PaymentMethodService {

    List<PaymentMethod> getAllByOrderId(String id);

    PaymentMethod add(PaymentMethod paymentMethod);

    Page<PaymentMethod> loadAndSearch(String orderCode, Boolean method, String customerFullname, Date startDate, Date endDate, Pageable pageable);

}
