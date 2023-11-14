package com.example.demo.repository;

import com.example.demo.entity.Photo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String> {

    List<Photo> getPhotoByRoomId(String id);

    @Transactional
    @Query(value = "DELETE FROM photo where id = ?1", nativeQuery = true)
    void deletePhotoById(String id);

    @Query(value = "select p.url from photo p where room_id = :id", nativeQuery = true)
    List<String> getUrlByIdRoom(@Param("id") String id);

}
