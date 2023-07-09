package com.example.demo.service;

import com.example.demo.entity.TypeRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TypeRoomService {

    Page<TypeRoom> getAll(Pageable pageable);

    TypeRoom getTypeRoomById(String id);

    TypeRoom add(TypeRoom typeRoom);

    void delete(String id);

}
