package com.example.demo.dto;

import com.example.demo.entity.Combo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ComboListDTO {
    private List<Combo> content;
    private Long totalElements;
    private int totalPages;
}
