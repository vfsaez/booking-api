package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Client;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Long id;
    @NotNull(message = "Name can't be null or empty")
    private String name;

}