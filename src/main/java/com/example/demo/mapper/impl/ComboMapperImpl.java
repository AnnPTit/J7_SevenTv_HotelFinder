package com.example.demo.mapper.impl;

import com.example.demo.dto.ComboDTO;
import com.example.demo.entity.Combo;
import com.example.demo.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComboMapperImpl implements EntityMapper<ComboDTO, Combo> {


    @Override
    public Combo toEntity(ComboDTO dto) {
        return null;
    }

    @Override
    public ComboDTO toDto(Combo combo) {
        ComboDTO comboDTO = new ComboDTO();
        comboDTO.setId(combo.getId());
        comboDTO.setComboCode(combo.getComboCode());
        comboDTO.setComboName(combo.getComboName());
        comboDTO.setNote(combo.getNote());
        comboDTO.setPrice(combo.getPrice());
        comboDTO.setComboServiceList(combo.getComboServiceList());
        return comboDTO;
    }

    @Override
    public List<Combo> toEntity(List<ComboDTO> dtoList) {
        return null;
    }

    @Override
    public List<ComboDTO> toDto(List<Combo> entityList) {
        List<ComboDTO> comboDTOS = new ArrayList<>();
        for (Combo combo : entityList) {
            comboDTOS.add(toDto(combo));
        }
        return comboDTOS;
    }
}
