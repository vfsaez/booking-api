package com.victorsaez.bookingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException(Long resourceId, Long userId) {
        super("Resource id " + resourceId + " is not owned by user id " + userId);
    }

    public AccessDeniedException(Long userId) {
        super("Not enough permissions user id " + userId);
    }
}
