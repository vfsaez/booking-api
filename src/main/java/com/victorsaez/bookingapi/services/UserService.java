package com.victorsaez.bookingapi.services;


import com.victorsaez.bookingapi.dto.UserDTO;

import java.util.List;

public interface UserService {
     List<UserDTO> findAll();

    UserDTO findById(Long id);
    
    UserDTO insert(UserDTO dto);

    UserDTO update(UserDTO dto);

    void delete(Long id);
}

