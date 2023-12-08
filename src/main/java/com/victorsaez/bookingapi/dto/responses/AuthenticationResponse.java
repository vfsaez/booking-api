package com.victorsaez.bookingapi.dto.responses;

public class AuthenticationResponse {
    private String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    // getter
    public String getJwt() {
        return jwt;
    }
}