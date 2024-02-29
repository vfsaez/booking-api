package com.victorsaez.bookingapi.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgeRequirementsException extends Exception {

    public AgeRequirementsException() {
        super("Age requirements not met. Age must be over 10 years old.");
    }
}
