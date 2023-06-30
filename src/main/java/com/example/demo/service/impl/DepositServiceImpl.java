package com.example.demo.service.impl;

import com.example.demo.entity.Deposit;
import com.example.demo.repository.DepositRepository;
import com.example.demo.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    private DepositRepository depositRepository;

    @Override
    public List<Deposit> getAll() {
        return depositRepository.findAll();
    }

    @Override
    public Deposit getDepositById(UUID id) {
        return depositRepository.getDepositById(id);
    }

    @Override
    public Deposit add(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    @Override
    public void delete(UUID id) {
         depositRepository.deleteById(id);
    }
}
