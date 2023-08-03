package com.example.demo.repository;


import com.example.demo.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {

    @Query(value = "select * from position where status =1", nativeQuery = true)
    List<Position> getAll();

    @Query(value = "Select * from position where position_name = \"ROLE_USER\"", nativeQuery = true)
    Position getIdPosition();
}
