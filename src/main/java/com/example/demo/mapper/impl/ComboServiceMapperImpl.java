package com.example.demo.mapper.impl;

import com.example.demo.dto.ComboServiceDTO;
import com.example.demo.entity.ComboService;
import com.example.demo.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComboServiceMapperImpl implements EntityMapper<ComboServiceDTO, ComboService> {


    @Override
    public ComboService toEntity(ComboServiceDTO dto) {
        return null;
    }

    @Override
    public ComboServiceDTO toDto(ComboService entity) {
        ComboServiceDTO comboServiceDTO = new ComboServiceDTO();
        comboServiceDTO.setId(entity.getId());
        comboServiceDTO.setService(entity.getService()); // Assuming you have a method to convert Service to ServiceDTO
        comboServiceDTO.setCombo(entity.getCombo()); // Assuming you have a method to convert Combo to ComboDTO
        comboServiceDTO.setPrice(entity.getPrice());
        return comboServiceDTO;
    }

    @Override
    public List<ComboService> toEntity(List<ComboServiceDTO> dtoList) {
        return null;
    }

    @Override
    public List<ComboServiceDTO> toDto(List<ComboService> entityList) {
        List<ComboServiceDTO> comboServiceDTOS = new ArrayList<>();
        for (ComboService comboService : entityList) {
            comboServiceDTOS.add(toDto(comboService));
        }
        return comboServiceDTOS;
    }
}
