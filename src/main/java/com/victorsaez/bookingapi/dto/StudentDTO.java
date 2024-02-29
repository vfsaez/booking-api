package com.victorsaez.bookingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    private Long id;
    @NotNull(message = "Name can't be null or empty")
    private String name;

    @NotNull(message = "Family Name can't be null or empty")
    private String familyName;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email can't be null or empty")
    private String email;

    @NotNull(message = "Date of Birth can't be null or empty")
    private Date dateOfBirth;

}