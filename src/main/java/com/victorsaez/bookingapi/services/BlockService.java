package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.dto.BookingDTO;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(BlockService.class);


    public BlockService(BlockRepository repository, PropertyRepository propertyRepository, BookingRepository bookingRepository,  PropertyService propertyService) {
        this.repository = repository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
    }

    public Page<BlockDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) currentUserDetails;
        Page<Block> blocks = customUserDetails.isAdmin() ?
                repository.findAll(pageable):
                repository.findAllByOwnerId(customUserDetails.getId(), pageable);
        return blocks.map(blockMapper::blockToBlockDTO);
    }

    public BlockDTO findById(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return blockMapper.blockToBlockDTO(repository.findById(id).map(block -> {
            if (customCurrentUserDetails.isAdmin() || block.getOwner().getId().equals(((CustomUserDetails) currentUserDetails).getId())) {
                return block;
            } else {
                throw new AccessDeniedException(id, ((CustomUserDetails) currentUserDetails).getId());
            }}).orElseThrow(() -> new BlockNotFoundException(id)));
    }

    public BlockDTO insert(BlockDTO dto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        Block block = blockMapper.blockDTOtoBlock(dto);
        block.setProperty(property);
        block.setOwner(customCurrentUserDetails.getUser());
        propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());
        Block createdBlock = repository.save(block);
        logger.info("user {} Block id {} created for property id {}", customCurrentUserDetails.getId(), createdBlock.getId(), createdBlock.getProperty().getId());
        return blockMapper.blockToBlockDTO(createdBlock);
    }


    public BlockDTO update(BlockDTO dto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Block existingBlock = repository.findById(dto.getId())
                .orElseThrow(() -> new BlockNotFoundException(dto.getId()));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        if (existingBlock.getStatus().equals(BlockStatus.CANCELLED) && !dto.getStatus().equals(BlockStatus.CANCELLED)) {
            propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());
        }

        existingBlock.setStartDate(dto.getStartDate());
        existingBlock.setEndDate(dto.getEndDate());
        existingBlock.setStatus(dto.getStatus());
        if (!customCurrentUserDetails.isAdmin()
                && !existingBlock.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }
        Block updatedBlock = repository.save(existingBlock);
        logger.info("user {} Block id {} updated for property id {}", customCurrentUserDetails.getId(), updatedBlock.getId(), updatedBlock.getProperty().getId());
        return blockMapper.blockToBlockDTO(updatedBlock);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BlockDTO dto = this.findById(id, currentUserDetails);
        logger.info("user {} Block id {} deleted for property id {}", customCurrentUserDetails.getId(), dto.getId(), dto.getPropertyId());
        repository.deleteById(id);
    }
}
