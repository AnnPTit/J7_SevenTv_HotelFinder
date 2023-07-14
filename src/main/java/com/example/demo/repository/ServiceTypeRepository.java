package com.example.demo.repository;

import com.example.demo.entity.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, String> {

    @Query(value = "select * from service_type where status =1", nativeQuery = true)
    Page<ServiceType> findAll(Pageable pageable);

    boolean existsByServiceTypeCode(String code);
}
