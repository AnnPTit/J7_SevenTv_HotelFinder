package com.example.demo.repository;

import com.example.demo.entity.Floor;
import com.example.demo.entity.TypeRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRoomRepository extends JpaRepository<TypeRoom, String> {

    @Query(value = "select * from type_room where status = 1", nativeQuery = true)
    Page<TypeRoom> findAll(Pageable pageable);

    @Query(value = "select * from type_room where\n" +
            "(type_room_code = ?1 or type_room_name like ?2) and status = 1 ", nativeQuery = true)
    Page<TypeRoom> findByCodeOrName(String code, String name, Pageable pageable);

    boolean existsByTypeRoomCode(String code);

}
