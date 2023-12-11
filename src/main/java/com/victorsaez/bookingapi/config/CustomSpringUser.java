package com.victorsaez.bookingapi.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomSpringUser extends User {

    private Long id;
    public CustomSpringUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}