package com.example.demo.repository;

import com.example.demo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String> {

    List<Photo> getPhotoByRoomId(String id);

}
