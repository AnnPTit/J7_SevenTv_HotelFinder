package com.example.demo.dto;

import com.example.demo.entity.Facility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FacilityRequestDTO {
    private String roomname;
    private Integer selectedChildren;
    private List<Double> priceRange;
    private List<Facility> selectedFacilities;
    private String typeRoomChose;
    private Integer currentPage;
    private Boolean isCrease;
    private Boolean isCreaseBook;
}
