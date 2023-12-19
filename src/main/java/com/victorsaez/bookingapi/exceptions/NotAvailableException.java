package com.victorsaez.bookingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotAvailableException extends RuntimeException {

    public NotAvailableException(String message) {
        super(message);
    }
}
