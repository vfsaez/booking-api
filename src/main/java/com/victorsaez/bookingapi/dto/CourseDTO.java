package com.victorsaez.bookingapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CourseDTO {

    private Long id;
    @NotNull(message = "Name can't be null or empty")
    @Size(min = 3, max = 25)
    private String name;

}
