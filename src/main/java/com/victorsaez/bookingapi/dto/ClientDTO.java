package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Client;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String streetName;
    private String city;
    private String state;
    private String homeNumber;
    private String zipCode;

}
