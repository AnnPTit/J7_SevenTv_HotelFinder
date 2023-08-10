package com.example.demo.service.impl;

import com.example.demo.entity.PaymentMethod;
import com.example.demo.repository.PaymentMethodRepository;
import com.example.demo.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethod add(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }
}
