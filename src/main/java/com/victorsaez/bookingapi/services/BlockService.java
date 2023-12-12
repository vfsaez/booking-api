package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.*;
import com.victorsaez.bookingapi.mappers.BlockMapper;
import com.victorsaez.bookingapi.repositories.BlockRepository;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.services.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockService {
    private final BlockRepository repository;
    private final PropertyRepository propertyRepository;

    private final PropertyService propertyService;

    private final BlockMapper blockMapper = BlockMapper.INSTANCE;

    public BlockService(BlockRepository repository, PropertyRepository propertyRepository, BookingRepository bookingRepository,  PropertyService propertyService) {
        this.repository = repository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
    }

    public Page<BlockDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        Page<Block> blocks = repository.findAll(pageable);
        return blocks.map(blockMapper::blockToBlockDTO);
    }

    public BlockDTO findById(Long id, UserDetails currentUserDetails) {
        return blockMapper.blockToBlockDTO(repository.findById(id)
                .orElseThrow(() -> new BlockNotFoundException(id)));
    }

    public BlockDTO insert(BlockDTO dto, UserDetails currentUserDetails) {
        CustomSpringUser customCurrentUserDetails = (CustomSpringUser) currentUserDetails;
        Property property = propertyRepository.findById(dto.getProperty().getId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getProperty().getId()));

        Block block = blockMapper.blockDTOtoBlock(dto);
        block.setProperty(property);
        block.setOwner(customCurrentUserDetails.getUser());
        propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());
        Block createdBlock = repository.save(block);

        return blockMapper.blockToBlockDTO(createdBlock);
    }


    public BlockDTO update(BlockDTO dto, UserDetails currentUserDetails) {
        CustomSpringUser customCurrentUserDetails = (CustomSpringUser) currentUserDetails;
        Block existingBlock = repository.findById(dto.getId())
                .orElseThrow(() -> new BlockNotFoundException(dto.getId()));

        Property property = propertyRepository.findById(dto.getProperty().getId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getProperty().getId()));

        if (existingBlock.getStatus().equals(BlockStatus.CANCELLED) && !dto.getStatus().equals(BlockStatus.CANCELLED)) {
            propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());
        }

        existingBlock.setStartDate(dto.getStartDate());
        existingBlock.setEndDate(dto.getEndDate());
        existingBlock.setStatus(dto.getStatus());
        if (!existingBlock.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }
        Block updatedBlock = repository.save(existingBlock);

        return blockMapper.blockToBlockDTO(updatedBlock);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        this.findById(id, currentUserDetails);
        repository.deleteById(id);
    }
}
