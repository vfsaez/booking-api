package com.victorsaez.bookingapi.dto.responses;

import com.victorsaez.bookingapi.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String jwt;
    private UserDTO user;
}