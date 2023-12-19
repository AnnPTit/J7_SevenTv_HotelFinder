package com.example.demo.service.impl;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.repository.PaymentMethodRepository;
import com.example.demo.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public List<PaymentMethod> getAllByOrderId(String id) {
        return paymentMethodRepository.getAllByOrderId(id);
    }

    @Override
    public PaymentMethod add(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Page<PaymentMethod> loadAndSearch(String orderCode, Boolean method, String customerFullname, Date startDate, Date endDate, Pageable pageable) {
        return paymentMethodRepository.loadAndSearch((orderCode != null && !orderCode.isEmpty()) ? orderCode : null,
                (method != null && !method.toString().isEmpty()) ? method : null,
                (customerFullname != null && !customerFullname.isEmpty() ? customerFullname : null),
                startDate,
                endDate,
                pageable);
    }
}
