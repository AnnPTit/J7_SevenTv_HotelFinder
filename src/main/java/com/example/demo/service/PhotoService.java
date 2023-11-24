package com.example.demo.service;

import com.example.demo.entity.Photo;
import com.example.demo.entity.Room;

import java.util.List;

public interface PhotoService {

    void save(List<Photo> photoList);

    Photo add(Photo photo);

    List<Photo> getPhotoByRoomId(String id);

    void delete(String id);

    Photo getPhotoById(String id);

    void deletePhotoById(String url);

    void deletePhoto(Photo photo);

    List<String> getUrlByIdRoom(String id);
    List<String> getUrlByIdBlog(String id);

}
