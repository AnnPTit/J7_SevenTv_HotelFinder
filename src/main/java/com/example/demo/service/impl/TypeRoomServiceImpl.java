package com.example.demo.service.impl;

import com.example.demo.dto.TypeRoomDTO;
import com.example.demo.entity.TypeRoom;
import com.example.demo.repository.TypeRoomRepository;
import com.example.demo.service.PhotoService;
import com.example.demo.service.TypeRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeRoomServiceImpl implements TypeRoomService {

    @Autowired
    private TypeRoomRepository typeRoomRepository;

    private final PhotoService photoService;

    @Override
    public List<TypeRoom> getList() {
        return typeRoomRepository.findAll();
    }

    @Override
    public Page<TypeRoom> getAll(Pageable pageable) {
        return typeRoomRepository.findAll(pageable);
    }

    @Override
    public Page<TypeRoomDTO> findByCodeOrName(String key, Pageable pageable) {
        return typeRoomRepository.findByCodeOrName(key, "%" + key + "%", pageable).map(item -> toDTO(item));
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

//    @Override
//    public TypeRoomDTO getTypeRoomByID(String id) {
//        return typeRoomRepository.getTypeRoomByID(id);
//    }

    public TypeRoomDTO toDTO(TypeRoom entity){
        TypeRoomDTO typeRoomDTO = new TypeRoomDTO();
        typeRoomDTO.setId(entity.getId());
        typeRoomDTO.setTypeRoomCode(entity.getTypeRoomCode());
        typeRoomDTO.setTypeRoomName(entity.getTypeRoomName());
        typeRoomDTO.setPricePerDay(entity.getPricePerDay());
        typeRoomDTO.setPricePerHours(entity.getPricePerHours());
        typeRoomDTO.setCapacity(entity.getCapacity());
        typeRoomDTO.setAdult(entity.getAdult());
        typeRoomDTO.setChildren(entity.getChildren());
        typeRoomDTO.setNote(entity.getNote());
        typeRoomDTO.setCreateAt(entity.getCreateAt());
        typeRoomDTO.setCreateBy(entity.getCreateBy());
        typeRoomDTO.setUpdateAt(entity.getUpdateAt());
        typeRoomDTO.setUpdatedBy(entity.getUpdatedBy());
        typeRoomDTO.setStatus(entity.getStatus());
        List<String> photo = photoService.getUrlByIdTypeRoom(entity.getId());
        if (photo.size() != 0) {
            typeRoomDTO.setPhotoDTOS(photo);
        }
        return typeRoomDTO;
    }
}
