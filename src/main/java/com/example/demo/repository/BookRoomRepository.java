package com.example.demo.repository;

import com.example.demo.entity.BookRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRoomRepository extends JpaRepository<BookRoom, String> {
}
