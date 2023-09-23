package com.example.demo.repository;

import com.example.demo.entity.ComboUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboUsedRepository extends JpaRepository<ComboUsed, String> {

    @Query(value = "SELECT * FROM combo_used where order_detail_id = ?1 ORDER BY create_at DESC", nativeQuery = true)
    List<ComboUsed> getAllByOrderDetailId(String id);

}
