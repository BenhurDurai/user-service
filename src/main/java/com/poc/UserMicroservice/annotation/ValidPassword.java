package com.poc.UserMicroservice.annotation;

import com.poc.UserMicroservice.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Password must be at least 6 characters long and contain at least one uppercase, one lowercase, one digit and one special character. ";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};

}
