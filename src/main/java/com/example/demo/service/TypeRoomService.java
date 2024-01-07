package com.example.demo.service;

import com.example.demo.entity.TypeRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface TypeRoomService {

    List<TypeRoom> getList();

    Page<TypeRoom> getAll(Pageable pageable);

    Page<TypeRoom> findByCodeOrName(String key, Pageable pageable);

    TypeRoom getTypeRoomById(String id);

    TypeRoom findByName(String name);

    TypeRoom add(TypeRoom typeRoom);

    void delete(String id);

    boolean existsByCode(String code);

    Integer countRoomCanBeBook(String typeRoomId , Date checkIn , Date checkOut );

}
