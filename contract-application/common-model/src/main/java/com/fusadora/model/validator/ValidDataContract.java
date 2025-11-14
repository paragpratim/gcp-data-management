package com.fusadora.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataContractValidator.class)
@Documented
public @interface ValidDataContract {
    String message() default "Invalid data contract";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}