package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountCode(String code);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByCitizenId(String citizenId);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    Page<Account> findAll(Pageable pageable);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    List<Account> getAll();

    @Query(value = "SELECT *\n" +
            "FROM account\n" +
            "WHERE status = 1\n" +
            "  AND ((:accountCode IS NULL OR account_code = :accountCode)\n" +
            "       OR (:fullname IS NULL OR fullname LIKE CONCAT('%', :fullname, '%')))\n" +
            "  AND (:positionId IS NULL OR position_id = :positionId)", nativeQuery = true)
    Page<Account> loadAndSearch(@Param("accountCode") String accountCode,
                                @Param("fullname") String fullname,
                                @Param("positionId") String positionId, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByCitizenId(String citizenId);
}
