package com.victorsaez.bookingapi.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends NotFoundException {

    public StudentNotFoundException(Long id) {
        super("Student", id);
    }
}
