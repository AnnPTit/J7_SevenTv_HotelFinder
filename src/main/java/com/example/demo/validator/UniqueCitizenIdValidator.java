package com.example.demo.validator;


import com.example.demo.repository.AccountRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueCitizenIdValidator implements ConstraintValidator<UniqueCitizenId, String> {

    @Autowired
    private AccountRepository accountRepository;

    public UniqueCitizenIdValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void initialize(UniqueCitizenId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String citizenId, ConstraintValidatorContext context) {
        if (citizenId == null || citizenId.isEmpty()) {
            return true;
        }
        return !accountRepository.existsByCitizenId(citizenId);
    }
}

