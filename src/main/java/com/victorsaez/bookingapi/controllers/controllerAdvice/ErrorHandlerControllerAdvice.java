package com.victorsaez.bookingapi.controllers.controllerAdvice;

import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
class ErrorHandlerControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        for (var violation : e.getConstraintViolations()) {
            error.getViolations().add(
                    new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        for (var fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    ValidationErrorResponse onAccessDeniedException(AccessDeniedException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.getViolations().add(new Violation("accessDenied", e.getMessage()));
        return error;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ValidationErrorResponse onNotFoundException(
            RuntimeException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.getViolations().add(
                new Violation("NotFoundException", e.getMessage()));
        return error;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ValidationErrorResponse onRuntimeException(
            RuntimeException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.getViolations().add(
                new Violation("RuntimeError", e.getMessage()));
        return error;
    }
}
