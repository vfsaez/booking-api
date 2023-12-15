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

    public BlockDTO insert(BlockDTO blockDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Property property = propertyRepository.findById(blockDto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockDto.getPropertyId()));

        Block block = blockMapper.blockDTOtoBlock(blockDto);
        block.setProperty(property);
        block.setOwner(customCurrentUserDetails.getUser());
        propertyService.checkPropertyAvailabilityOnPeriod(property, blockDto.getStartDate(), blockDto.getEndDate());
        Block createdBlock = repository.save(block);
        logger.info("user {} Block id {} created for property id {}", customCurrentUserDetails.getId(), createdBlock.getId(), createdBlock.getProperty().getId());
        return blockMapper.blockToBlockDTO(createdBlock);
    }


    public BlockDTO update(BlockDTO blockDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Block existingBlock = repository.findById(blockDto.getId())
                .orElseThrow(() -> new BlockNotFoundException(blockDto.getId()));

        Property property = propertyRepository.findById(blockDto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(blockDto.getPropertyId()));

        if (existingBlock.getStatus().equals(BlockStatus.CANCELLED) && !blockDto.getStatus().equals(BlockStatus.CANCELLED)) {
            propertyService.checkPropertyAvailabilityOnPeriod(property, blockDto.getStartDate(), blockDto.getEndDate());
        }

        existingBlock.setStartDate(blockDto.getStartDate());
        existingBlock.setEndDate(blockDto.getEndDate());
        existingBlock.setStatus(blockDto.getStatus());
        if (!customCurrentUserDetails.isAdmin()
                && !existingBlock.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(blockDto.getId(), customCurrentUserDetails.getId());
        }
        Block updatedBlock = repository.save(existingBlock);
        logger.info("user {} Block id {} updated for property id {}", customCurrentUserDetails.getId(), updatedBlock.getId(), updatedBlock.getProperty().getId());
        return blockMapper.blockToBlockDTO(updatedBlock);
    }

    public BlockDTO patch(Long id, BlockDTO blockDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Block existingBlock = repository.findById(id)
                .orElseThrow(() -> new BlockNotFoundException(id));

        if (blockDto.getStartDate() != null) {
            existingBlock.setStartDate(blockDto.getStartDate());
        }
        if (blockDto.getEndDate() != null) {
            existingBlock.setEndDate(blockDto.getEndDate());
        }
        if (blockDto.getStatus() != null) {
            existingBlock.setStatus(blockDto.getStatus());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingBlock.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(id, customCurrentUserDetails.getId());
        }

        Block updatedBlock = repository.save(existingBlock);
        logger.info("user {} Block id {} patched for property id {}", customCurrentUserDetails.getId(), updatedBlock.getId(), updatedBlock.getProperty().getId());
        return blockMapper.blockToBlockDTO(updatedBlock);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BlockDTO blockDto = this.findById(id, currentUserDetails);
        logger.info("user {} Block id {} deleted for property id {}", customCurrentUserDetails.getId(), blockDto.getId(), blockDto.getPropertyId());
        repository.deleteById(id);
    }
}
