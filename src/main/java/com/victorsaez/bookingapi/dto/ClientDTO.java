package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Client;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Long id;
    private String name;
    private int totalRentals;

}