package com.example.demo.validator;


import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueCitizenIdValidator implements ConstraintValidator<UniqueCitizenId, String> {

    @Autowired
    private AccountService accountService;

//    public UniqueCitizenIdValidator(AccountService accountService) {
//        this.accountService = accountService;
//    }

    @Override
    public void initialize(UniqueCitizenId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String citizenId, ConstraintValidatorContext context) {
        if (citizenId == null || citizenId.isEmpty()) {
            return true;
        }
        return !accountService.existsByCitizenId(citizenId);
    }
}

