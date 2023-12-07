package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.BlockNotFoundException;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.repositories.BlockRepository;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.services.BlockService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockServiceImpl implements BlockService {
    private final BlockRepository repository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;

    private final BookingRepository bookingRepository;

    public BlockServiceImpl(BlockRepository repository, ClientRepository clientRepository, PropertyRepository propertyRepository, BookingRepository bookingRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<BlockDTO> findAll() {
        List<Block> list = repository.findAll();
        return list.stream().map(BlockDTO::new).collect(Collectors.toList());
    }

    @Override
    public BlockDTO findById(Long id) {
        return new BlockDTO(repository.findById(id)
                .orElseThrow(() -> new BlockNotFoundException(id)));
    }

    @Override
    public BlockDTO insert(BlockDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getClientId()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        Block block = new Block(dto);
        block.setClient(client);
        block.setProperty(property);

        //check for existing bookings where status is not 'CANCELLED' on the same dates and return error if found
        List<Booking> existingBookings = bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BookingStatus.CANCELLED);

        if (!existingBookings.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
        }

        //check for existing blocks where status is not 'CANCELLED' on the same dates and return error if found
        List<Block> existingBlocks = repository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BlockStatus.CANCELLED);

        if (!existingBlocks.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
        }

        Block createdBlock = repository.save(block);

        return new BlockDTO(createdBlock);
    }


    @Override
    public BlockDTO update(BlockDTO dto) {
        Block existingBlock = repository.findById(dto.getId())
                .orElseThrow(() -> new BlockNotFoundException(dto.getId()));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        if (existingBlock.getStatus().equals(BlockStatus.CANCELLED) && !dto.getStatus().equals(BlockStatus.CANCELLED)) {
            //check for existing bookings where status is not 'CANCELLED' on the same dates and return error if found
            List<Booking> existingBookings = bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BookingStatus.CANCELLED);

            if (!existingBookings.isEmpty()) {
                throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
            }

            //check for existing blocks where status is not 'CANCELLED' on the same dates and return error if found
            List<Block> existingBlocks = repository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BlockStatus.CANCELLED);

            if (!existingBlocks.isEmpty()) {
                throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
            }
        }

        existingBlock.setStartDate(dto.getStartDate());
        existingBlock.setEndDate(dto.getEndDate());
        existingBlock.setStatus(dto.getStatus());
        Block updatedBlock = repository.save(existingBlock);

        return new BlockDTO(updatedBlock);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
