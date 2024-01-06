package com.example.demo.service.impl;

import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.TypeRoomRepository;
import com.example.demo.service.TypeRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeRoomServiceImpl implements TypeRoomService {

    @Autowired
    private TypeRoomRepository typeRoomRepository;

    @Override
    public List<TypeRoom> getList() {
        return typeRoomRepository.findAll();
    }

    @Override
    public Page<TypeRoom> getAll(Pageable pageable) {
        return typeRoomRepository.findAll(pageable);
    }

    @Override
    public Page<TypeRoom> findByCodeOrName(String key, Pageable pageable) {
        return typeRoomRepository.findByCodeOrName(key, "%" + key + "%", pageable);
    }

    @Override
    public TypeRoom getTypeRoomById(String id) {
        return typeRoomRepository.findById(id).orElse(null);
    }

    @Override
    public TypeRoom findByName(String name) {
        List<TypeRoom> list = typeRoomRepository.findByName(name);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public TypeRoom add(TypeRoom typeRoom) {
        try {
            return typeRoomRepository.save(typeRoom);
        } catch (Exception e) {
            System.out.println("Add Error!");
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            typeRoomRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Delete Error!");
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return typeRoomRepository.existsByTypeRoomCode(code);
    }
}
