package com.victorsaez.bookingapi.services;


import com.victorsaez.bookingapi.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
     List<UserDTO> findAll(UserDetails currentUserDetails);

    UserDTO findById(Long id, UserDetails currentUserDetails);
    
    UserDTO insert(UserDTO dto, UserDetails currentUserDetails);

    UserDTO update(UserDTO dto, UserDetails currentUserDetails);

    void delete(Long id, UserDetails currentUserDetails);
}

