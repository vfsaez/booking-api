package com.victorsaez.bookingapi.controllers.controllerAdvice;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private List<Violation> violations = new ArrayList<>();
    private Date timestamp = new Date();
    private int status;
}
