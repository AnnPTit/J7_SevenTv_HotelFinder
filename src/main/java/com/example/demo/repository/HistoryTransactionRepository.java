package com.example.demo.repository;

import com.example.demo.entity.HistoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryTransactionRepository extends JpaRepository<HistoryTransaction, String> {
}
