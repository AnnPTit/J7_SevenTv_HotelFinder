package com.example.demo.repository;

import com.example.demo.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {


}
