package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.ClientDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ClientService {

    List<ClientDTO> findAll(UserDetails currentUserDetails);

    ClientDTO findById(Long id, UserDetails currentUserDetails);

    ClientDTO insert(ClientDTO dto, UserDetails currentUserDetails);

    ClientDTO update(ClientDTO dto, UserDetails currentUserDetails);

     void delete(Long id, UserDetails currentUserDetails);
}




