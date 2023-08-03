package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;
import java.util.Date;

public class MinAgeValidator implements ConstraintValidator<MinAge, Date> {
    private int minAge;

    @Override
    public void initialize(MinAge minAge) {
        this.minAge = minAge.value();
    }

    @Override
    public boolean isValid(Date birthDate, ConstraintValidatorContext constraintValidatorContext) {
        if (birthDate == null) {
            return true;
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if (birthCalendar.get(Calendar.MONTH) > today.get(Calendar.MONTH) ||
                (birthCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) && birthCalendar.get(Calendar.DATE) > today.get(Calendar.DATE))) {
            years--;
        }

        return years >= minAge;
    }
}

