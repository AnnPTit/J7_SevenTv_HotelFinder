package com.example.demo.service.impl;

import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.TypeRoomRepository;
import com.example.demo.service.TypeRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TypeRoomServiceImpl implements TypeRoomService {

    @Autowired
    private TypeRoomRepository typeRoomRepository;

    @Override
    public Page<TypeRoom> getAll(Pageable pageable) {
        return typeRoomRepository.findAll(pageable);
    }

    @Override
    public TypeRoom getTypeRoomById(String id) {
        return typeRoomRepository.findById(id).orElse(null);
    }

    @Override
    public TypeRoom add(TypeRoom typeRoom) {
        try {
            return typeRoomRepository.save(typeRoom);
        } catch (Exception e){
            System.out.println("Add Error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            typeRoomRepository.deleteById(id);
        } catch (Exception e){
            System.out.println("Delete Error!");
        }
    }
}
