package com.example.demo.repository;

import com.example.demo.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {
}
