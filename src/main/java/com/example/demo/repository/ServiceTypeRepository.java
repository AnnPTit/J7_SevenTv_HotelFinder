package com.example.demo.repository;

import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, String> {

    @Query(value = "select * from service_type where status =1 order by update_at desc", nativeQuery = true)
    Page<ServiceType> findAll(Pageable pageable);


    @Query(value = "select * from service_type where status =1 order by update_at desc", nativeQuery = true)
    List<ServiceType> getAll();

    @Query(value = "select * from service_type where\n" +
            "(service_type_code = ?1 or service_type_name like ?2) and status = 1 order by update_at desc", nativeQuery = true)
    Page<ServiceType> findByCodeOrName(String code, String name, Pageable pageable);

    boolean existsByServiceTypeCode(String code);
}
