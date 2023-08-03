package com.example.demo.repository;

import com.example.demo.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {

    @Query(value = "select *\n" +
            "from unit where status = 1 order by create_at desc" , nativeQuery = true)
    List<Unit> getAll();
}
