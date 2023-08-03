package com.example.demo.service.impl;

import com.example.demo.entity.BookRoom;
import com.example.demo.repository.BookRoomRepository;
import com.example.demo.service.BookRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookRoomServiceImpl implements BookRoomService {

    @Autowired
    private BookRoomRepository bookRoomRepository;

    @Override
    public List<BookRoom> getList() {
        return bookRoomRepository.findAll();
    }

    @Override
    public Page<BookRoom> getAll(Pageable pageable) {
        return bookRoomRepository.findAll(pageable);
    }

    @Override
    public BookRoom getBookRoomById(String id) {
        return bookRoomRepository.findById(id).orElse(null);
    }

    @Override
    public BookRoom add(BookRoom bookRoom) {
        try {
            return bookRoomRepository.save(bookRoom);
        } catch (Exception e) {
            System.out.println("Add error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            bookRoomRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }
}
