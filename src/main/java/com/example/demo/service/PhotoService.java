package com.example.demo.service;

import com.example.demo.entity.Photo;

import java.util.List;

public interface PhotoService {

    void save(List<Photo> photoList);

    Photo add(Photo photo);

    List<Photo> getPhotoByRoomId(String id);

}
