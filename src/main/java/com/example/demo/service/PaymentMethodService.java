package com.example.demo.service;

import com.example.demo.entity.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {

    List<PaymentMethod> getAllByOrderId(String id);

    PaymentMethod add(PaymentMethod paymentMethod);

}
