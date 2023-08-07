package com.example.demo.repository;

import com.example.demo.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {

    Deposit findByDepositCode(String code);

}
