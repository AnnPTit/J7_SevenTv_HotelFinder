package com.example.demo.service.impl;

import com.example.demo.entity.Photo;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public void save(List<Photo> photoList) {
        try {
            photoRepository.saveAll(photoList);
        } catch (Exception e) {
            System.out.println("Add error!");
            e.printStackTrace();
        }
    }

    @Override
    public Photo add(Photo photo) {
        return photoRepository.save(photo);
    }

    @Override
    public List<Photo> getPhotoByRoomId(String id) {
        return photoRepository.getPhotoByRoomId(id);
    }

    @Override
    public List<Photo> getPhotoByBlog(String id) {
        return photoRepository.getPhotoByBlog(id);
    }

    @Override
    public List<Photo> getPhotoByTypeRoom(String id) {
        return photoRepository.getPhotoByTypeRoom(id);
    }

    @Override
    public void delete(String id) {
        try {
            photoRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete error!");
        }
    }

    @Override
    public Photo getPhotoById(String id) {
        return photoRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePhotoById(String url) {
        photoRepository.deletePhotoById(url);
    }

    @Override
    public void deletePhoto(Photo photo) {
        photoRepository.delete(photo);
    }

    @Override
    public List<String> getUrlByIdRoom(String id) {
        return photoRepository.getUrlByIdRoom(id);
    }

    @Override
    public List<String> getUrlByIdBlog(String id) {
        return photoRepository.getUrlByIdBlog(id);
    }

    @Override
    public List<String> getUrlByIdTypeRoom(String id) {
        return photoRepository.getUrlByIdTypeRoom(id);
    }

}
