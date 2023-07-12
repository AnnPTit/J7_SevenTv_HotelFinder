package com.example.demo.service;

import com.example.demo.entity.BookRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRoomService {

    Page<BookRoom> getAll(Pageable pageable);

    BookRoom getBookRoomById(String id);

    BookRoom add(BookRoom bookRoom);

    void delete(String id);
    
}
