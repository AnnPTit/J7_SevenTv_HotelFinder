package com.example.demo.repository;

import com.example.demo.entity.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

    @Query(value = "select * from floor where status = 1 ORDER BY update_at", nativeQuery = true)
    Page<Floor> findAll(Pageable pageable);

    @Query(value = "select * from floor where\n" +
            "(floor_code like ?1 or floor_name like ?2) and status = 1 ORDER BY update_at", nativeQuery = true)
    Page<Floor> findByCodeOrName(String code, String name, Pageable pageable);

    boolean existsByFloorCode(String code);

}
