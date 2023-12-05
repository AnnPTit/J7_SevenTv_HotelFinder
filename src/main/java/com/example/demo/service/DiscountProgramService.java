package com.example.demo.service;

import com.example.demo.entity.DiscountProgram;

import java.util.List;

public interface DiscountProgramService {

    List<DiscountProgram> loadDiscountByCondition();

    void updateNumberOfApplication(String id);

}
