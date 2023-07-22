package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountCode(String code);
    Optional<Account> findByEmail(String email);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    Page<Account> findAll(Pageable pageable);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    List<Account> getAll();

    @Query(value = "select * from account where\n" +
            "(account_code = ?1 or fullname like ?2) and status = 1 ", nativeQuery = true)
    Page<Account> findByCodeOrName(String code, String name, Pageable pageable);
}
