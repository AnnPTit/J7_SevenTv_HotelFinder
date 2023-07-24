package com.example.demo.repository;


import com.example.demo.entity.ComboService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ComboServiceRepository extends JpaRepository<ComboService, String> {
    @Query(value = "select *\n" +
            "from combo_service where status =1 order by create_at", nativeQuery = true)
    Page<ComboService> findAll(Pageable pageable);
}
