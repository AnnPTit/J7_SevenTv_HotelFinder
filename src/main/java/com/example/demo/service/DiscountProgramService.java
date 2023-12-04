package com.example.demo.service;

import com.example.demo.dto.DiscountProgramDTO;
import com.example.demo.entity.DiscountProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DiscountProgramService {

    DiscountProgram getOne(String id);

    String generateDiscountProgramCode();

    Page<DiscountProgramDTO> loadAndSearch(String name, String code, Pageable pageable);

    Boolean add(DiscountProgram discountProgram);

    DiscountProgram findById(String id);

    List<DiscountProgram> loadDiscountByCondition();

    void updateNumberOfApplication(String id);

}
