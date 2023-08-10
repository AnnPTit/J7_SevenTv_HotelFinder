package com.example.demo.service;

import com.example.demo.entity.Deposit;

import java.util.List;
import java.util.UUID;

public interface DepositService {

    List<Deposit> getAll();

    Deposit getDepositById(String id);

    Deposit getByCode(String code);

    Deposit add(Deposit deposit);

    void delete(String id);

}
