package com.example.demo.validator;
import com.example.demo.repository.AccountRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private AccountRepository accountRepository;

    public UniqueEmailValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return !accountRepository.existsByEmail(email);
    }
}

