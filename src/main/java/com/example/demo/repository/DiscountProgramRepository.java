package com.example.demo.repository;


import com.example.demo.entity.DiscountProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Repository

public interface DiscountProgramRepository extends JpaRepository<DiscountProgram, String> {

    @Query(value = "select * from discount_program where id =:id and status =1 ", nativeQuery = true)
    DiscountProgram getOne(String id);

    @Query(value = "select * from discount_program", nativeQuery = true)
    List<DiscountProgram> getAllDiscountProgram();

    @Query(value = "SELECT *\n" +
            "FROM discount_program\n" +
            " WHERE ((:name is null or name LIKE CONCAT('%', :name ,'%'))\n " +
            " OR (:code is null or code LIKE CONCAT('%', :code ,'%')))\n" +
            " and status = 1 order by update_at desc", nativeQuery = true)
    Page<DiscountProgram> loadAndSearch(@Param("name") String name, @Param("code") String code, Pageable pageable);

    @Query(value = "SELECT * FROM discount_program " +
            "WHERE number_of_application > 0 AND DATE(end_date) > DATE(current_date())" +
            " AND status = 1 ORDER BY update_at DESC", nativeQuery = true)
    List<DiscountProgram> loadDiscountByCondition();

    @Modifying
    @Transactional
    @Query(value = "UPDATE DiscountProgram SET numberOfApplication = numberOfApplication - 1 WHERE id = :id")
    void updateNumberOfApplication(@Param("id") String id);
}
