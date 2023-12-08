package com.victorsaez.bookingapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victorsaez.bookingapi.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;

    @JsonIgnore
    private String password;
    private String roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }
}
