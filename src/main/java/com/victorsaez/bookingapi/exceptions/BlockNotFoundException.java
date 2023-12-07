package com.victorsaez.bookingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BlockNotFoundException extends RuntimeException{

    public BlockNotFoundException(Long id) {
        super("Block not found with ID: " + id);
    }
}
