package com.example.demo.repository;

import com.example.demo.entity.Photo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String> {

    List<Photo> getPhotoByRoomId(String id);

    List<Photo> getPhotoByBlog(String blog);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM photo WHERE id = ?1", nativeQuery = true)
    void deletePhotoById(String id);


    @Query(value = "select p.url from photo p where room_id = :id", nativeQuery = true)
    List<String> getUrlByIdRoom(@Param("id") String id);
    @Query(value = "select p.url from photo p where blog_id = :id", nativeQuery = true)
    List<String> getUrlByIdBlog(@Param("id") String id);
}
