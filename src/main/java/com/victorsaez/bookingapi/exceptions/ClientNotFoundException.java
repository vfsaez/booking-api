package com.victorsaez.bookingapi.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFoundException extends NotFoundException {

    public ClientNotFoundException(Long id) {
        super(id);
    }
}
