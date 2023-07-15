package com.example.demo.repository;

import com.example.demo.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    @Query(value = "select * from service where status =1", nativeQuery = true)
    Page<Service> findAll(Pageable pageable);

    boolean existsByServiceCode(String code);

}
