package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.ClientDTO;
import java.util.List;

public interface ClientService {

    List<ClientDTO> findAll();

    ClientDTO findById(Long id);

    ClientDTO insert(ClientDTO dto);

    ClientDTO update(ClientDTO dto);

     void delete(Long id);
}




