package com.example.demo.service.impl;

import com.example.demo.entity.HistoryTransaction;
import com.example.demo.repository.HistoryTransactionRepository;
import com.example.demo.service.HistoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryTransactionServiceImpl implements HistoryTransactionService {

    @Autowired
    private HistoryTransactionRepository historyTransactionRepository;

    @Override
    public HistoryTransaction add(HistoryTransaction historyTransaction) {
        return historyTransactionRepository.save(historyTransaction);
    }
}
