package com.victorsaez.bookingapi.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomSpringUser extends User {

    private Long id;

    private com.victorsaez.bookingapi.entities.User user;

    public CustomSpringUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public void setUser(com.victorsaez.bookingapi.entities.User user) {
        this.user = user;
    }
    public com.victorsaez.bookingapi.entities.User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}