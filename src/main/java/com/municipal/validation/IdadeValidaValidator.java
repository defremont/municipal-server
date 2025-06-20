package com.municipal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class IdadeValidaValidator implements ConstraintValidator<IdadeValida, LocalDate> {
    
    private int min;
    private int max;
    
    @Override
    public void initialize(IdadeValida constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    
    @Override
    public boolean isValid(LocalDate dataNascimento, ConstraintValidatorContext context) {
        if (dataNascimento == null) {
            return true; // Let @NotNull handle null validation
        }
        
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        
        if (idade < min || idade > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Idade deve estar entre %d e %d anos (idade atual: %d)", min, max, idade)
            ).addConstraintViolation();
            return false;
        }
        
        return true;
    }
} 