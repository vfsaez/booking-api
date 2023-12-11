package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.BlockDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public interface BlockService {

    List<BlockDTO> findAll(UserDetails currentUserDetails);

    BlockDTO findById(Long id, UserDetails currentUserDetails);

    BlockDTO insert(BlockDTO block, UserDetails currentUserDetails);

    BlockDTO update(BlockDTO block, UserDetails currentUserDetails);

    void delete(Long id, UserDetails currentUserDetails);
}




