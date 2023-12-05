package com.example.demo.repository;

import com.example.demo.entity.DiscountProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DiscountProgramRepository extends JpaRepository<DiscountProgram, String> {

    @Query(value = "SELECT * FROM discount_program " +
            "WHERE number_of_application > 0 AND DATE(end_date) > DATE(current_date())" +
            " AND status = 1 ORDER BY update_at DESC", nativeQuery = true)
    List<DiscountProgram> loadDiscountByCondition();

    @Modifying
    @Transactional
    @Query(value = "UPDATE DiscountProgram SET numberOfApplication = numberOfApplication - 1 WHERE id = :id")
    void updateNumberOfApplication(@Param("id") String id);

}
