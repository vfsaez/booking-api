package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.BlockDTO;

import java.util.List;


public interface BlockService {

    List<BlockDTO> findAll();

    BlockDTO findById(Long id);

    BlockDTO insert(BlockDTO block);

    BlockDTO update(BlockDTO block);

    void delete(Long id);
}




