package com.example.demo.service.impl;

import com.example.demo.constant.Constant;
import com.example.demo.dto.DiscountProgramDTO;
import com.example.demo.entity.DiscountProgram;
import com.example.demo.repository.DiscountProgramRepository;
import com.example.demo.service.DiscountProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DiscountProgramServiceImpl implements DiscountProgramService {

    @Autowired
    private DiscountProgramRepository discountProgramRepository;

    @Override
    public DiscountProgram getOne(String id) {
        return (DiscountProgram) discountProgramRepository.getOne(id);
    }

    @Override
    public String generateDiscountProgramCode() {
        String ctgg = "CTGG";
        ctgg += String.format("%2d", discountProgramRepository.getAllDiscountProgram().size() + 1);
        return ctgg;
    }

    @Override
    public Page<DiscountProgramDTO> loadAndSearch(String name, String code, Pageable pageable) {
        return discountProgramRepository.loadAndSearch(((name != null && !name.isEmpty()) ? "%" + name + "%" : null),
                ((code != null && !code.isEmpty()) ? "%" + code + "%" : null), pageable).map(this::fromEntity);
    }

    @Override
    public Boolean add(DiscountProgram discountProgram) {
        try {
            discountProgramRepository.save(discountProgram);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public DiscountProgram findById(String id) {
        return discountProgramRepository.findById(id).orElse(null);
    }

    public DiscountProgramDTO fromEntity(DiscountProgram entity) {
        DiscountProgramDTO dto = new DiscountProgramDTO();
        // Copy dữ liệu từ entity sang DTO
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setMinimumInvoice(entity.getMinimumInvoice());
        dto.setReduceValue(entity.getReduceValue());
        dto.setNumberOfApplication(entity.getNumberOfApplication());
        dto.setMaximumReductionValue(entity.getMaxiumumReductionValue());
        dto.setStartDay(entity.getStartDay());
        dto.setEndDate(entity.getEndDate());
        dto.setCreateAt(entity.getCreateAt());
        dto.setCreateBy(entity.getCreateBy());
        dto.setUpdateAt(entity.getUpdateAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setDeleted(entity.getDeleted());

        // Tính toán và thiết lập trường trạng thái
        Date currentDate = new Date();
        if (entity.getStartDay().after(currentDate)) {
            dto.setTextStatus(Constant.DISCOUNT_PROGRAM.NOT_EFFECTIVE_YET);
        } else if (entity.getEndDate().after(currentDate) && entity.getStartDay().before(currentDate)) {
            dto.setTextStatus(Constant.DISCOUNT_PROGRAM.ON_GOING);
        } else {
            dto.setTextStatus(Constant.DISCOUNT_PROGRAM.EXPIRED);
        }

        return dto;
    }

//    private DiscountProgram toEntity(DiscountProgramDTO dto) {
//        DiscountProgram entity = new DiscountProgram();
//        entity.setId(dto.getId());
//        entity.setName(dto.getName());
//        entity.setCreateAt(dto.getCreateAt());
//        entity.setCreateBy(dto.getCreateBy());
//        entity.setStatus(dto.getStatus());
//        entity.setUpdateAt(dto.getUpdateAt());
//        entity.setUpdatedBy(dto.getUpdatedBy());
//        entity.setEndDate(dto.getEndDate());
//        entity.setStartDay(dto.getStartDay());
//        entity.setMinimumInvoice(dto.getMinimumInvoice());
//        entity.setNumberOfApplication(dto.getNumberOfApplication());
//        entity.setReduceValue(dto.getReduceValue());
//        entity.setDeleted(dto.getDeleted());
//
//        return entity;
//    }

    @Override
    public List<DiscountProgram> loadDiscountByCondition() {
        return discountProgramRepository.loadDiscountByCondition();
    }

    @Override
    public void updateNumberOfApplication(String id) {
        discountProgramRepository.updateNumberOfApplication(id);
    }

}
