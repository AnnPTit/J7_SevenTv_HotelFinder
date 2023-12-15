package com.example.demo.service;

import com.example.demo.dto.Message;

public interface HomeService {
    Message cancelOrder(String id, Integer oddStt, String refuseReason);
}
