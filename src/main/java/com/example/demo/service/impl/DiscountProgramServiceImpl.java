package com.example.demo.service.impl;

import com.example.demo.entity.DiscountProgram;
import com.example.demo.repository.DiscountProgramRepository;
import com.example.demo.service.DiscountProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountProgramServiceImpl implements DiscountProgramService {

    @Autowired
    private DiscountProgramRepository discountProgramRepository;

    @Override
    public List<DiscountProgram> loadDiscountByCondition() {
        return discountProgramRepository.loadDiscountByCondition();
    }

    @Override
    public void updateNumberOfApplication(String id) {
        discountProgramRepository.updateNumberOfApplication(id);
    }

}
