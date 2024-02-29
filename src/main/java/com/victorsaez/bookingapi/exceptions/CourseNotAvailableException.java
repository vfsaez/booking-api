package com.victorsaez.bookingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CourseNotAvailableException extends NotAvailableException {

    public CourseNotAvailableException(Long id, String name, Date startDate, Date endDate) {
        super("Course \""+ name + "\" Id: " + id + "  - not available trough given dates: " + startDate + " - " + endDate);
    }

    public CourseNotAvailableException() {
        super("Course is not available trough given dates.");
    }
}
