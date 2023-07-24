package com.example.demo.repository;

import com.example.demo.entity.Combo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ComboRepository extends JpaRepository<Combo, String> {
    @Query(value = "select *\n" +
            "from combo where status =1 order by create_at", nativeQuery = true)
    Page<Combo> findAll(Pageable pageable);

    boolean existsByComboCode(String code);
}
