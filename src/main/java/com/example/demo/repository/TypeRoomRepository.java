package com.example.demo.repository;

import com.example.demo.entity.TypeRoom;
import lombok.experimental.PackagePrivate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeRoomRepository extends JpaRepository<TypeRoom, String> {

    @Query(value = "select * from type_room where status = 1 ORDER BY update_at desc", nativeQuery = true)
    Page<TypeRoom> findAll(Pageable pageable);

    @Query(value = "select * from type_room where status = 1", nativeQuery = true)
    List<TypeRoom> findAll();

    @Query(value = "select * from type_room where\n" +
            "(type_room_code = ?1 or type_room_name like ?2) and status = 1 ORDER BY update_at desc", nativeQuery = true)
    Page<TypeRoom> findByCodeOrName(String code, String name, Pageable pageable);

    @Query(value = "select * from type_room tr where tr.type_room_name =:name", nativeQuery = true)
    List<TypeRoom> findByName(@Param("name") String name);

    boolean existsByTypeRoomCode(String code);

}
