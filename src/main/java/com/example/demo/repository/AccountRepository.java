package com.example.demo.repository;

import com.example.demo.entity.Account;
import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountCode(String code);

    Optional<Account> findByEmail(String email);

    @Query(value = "select * from account a where a.email =:email and a.status =1", nativeQuery = true)
    List<Account> findByEmailLs(@Param("email") String email);

    Account findByEmailAndStatus(String email, Integer status);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    Page<Account> findAll(Pageable pageable);

    Account findAccountByIdAndStatus(String id, Integer status);

    @Query(value = "select * from account where status =1", nativeQuery = true)
    List<Account> getAll();

    @Query(value = "SELECT *\n" +
            "FROM account\n" +
            "WHERE ((:accountCode IS NULL OR account_code = :accountCode)\n" +
            "       OR (:fullname IS NULL OR fullname LIKE CONCAT('%', :fullname, '%')))\n" +
            "  AND (:positionId IS NULL OR position_id = :positionId) and status = 1 order by update_at desc", nativeQuery = true)
    Page<Account> loadAndSearch(@Param("accountCode") String accountCode,
                                @Param("fullname") String fullname,
                                @Param("positionId") String positionId, Pageable pageable);

    @Query(value = "SELECT * FROM account where account_code = 'TK00'", nativeQuery = true)
    Account getAccountByCode();

    @Query(value = "SELECT * FROM account where account_code =:codes ", nativeQuery = true)
    List<Account> getAccountByCode(@Param("codes") String codes);

    boolean existsByEmail(String email);

    boolean existsByCitizenId(String citizenId);

    @Modifying
    @Query(value = "update account a set a.password =:password where a.id =:id", nativeQuery = true)
    void updatePass(@Param("password") String password, @Param("id") String id);
}
