package com.example.demo.repository;

import com.example.demo.entity.ComboUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComboUsedRepository extends JpaRepository<ComboUsed, UUID> {
}
