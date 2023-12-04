package com.example.demo.repository;

import com.example.demo.dto.DiscountProgramDTO;
import com.example.demo.entity.DiscountProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
