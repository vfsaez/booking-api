package com.victorsaez.bookingapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victorsaez.bookingapi.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotNull(message = "Username cannot be null")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String username;

    @JsonIgnore
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    private String roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }
}
