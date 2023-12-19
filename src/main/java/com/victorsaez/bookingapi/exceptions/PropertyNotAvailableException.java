package com.victorsaez.bookingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PropertyNotAvailableException extends NotAvailableException {

    public PropertyNotAvailableException(Long id, String name, Date startDate, Date endDate) {
        super("Property \""+ name + "\" Id: " + id + "  - not available trough given dates: " + startDate + " - " + endDate);
    }

    public PropertyNotAvailableException() {
        super("Property is not available trough given dates.");
    }
}
