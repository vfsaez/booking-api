package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.BookingNotFoundException;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.mappers.BookingMapper;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.repositories.BlockRepository;

import com.victorsaez.bookingapi.services.BookingService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;
    private final BlockRepository blockRepository;

    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;

    public BookingServiceImpl(BookingRepository repository, ClientRepository clientRepository, PropertyRepository propertyRepository, BlockRepository blockRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.blockRepository = blockRepository;
    }

    @Override
    public List<BookingDTO> findAll() {
        List<Booking> bookings = repository.findAll();
        return bookings.stream()
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findById(Long id) {
        return bookingMapper.bookingToBookingDTO(repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id)));
    }

    @Override
    public BookingDTO insert(BookingDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getClientId()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        Booking booking = bookingMapper.bookingDTOtoBooking(dto);
        booking.setClient(client);
        booking.setProperty(property);

        //check for existing bookings where status is not 'CANCELLED' on the same dates and return error if found
        List<Booking> existingBookings = repository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BookingStatus.CANCELLED);

        if (!existingBookings.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
        }

        //check for existing blocks where status is not 'CANCELLED' on the same dates and return error if found
        List<Block> existingBlocks = blockRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BlockStatus.CANCELLED);

        if (!existingBlocks.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
        }

        Booking createdBooking = repository.save(booking);

        return bookingMapper.bookingToBookingDTO(createdBooking);
    }


    @Override
    public BookingDTO update(BookingDTO dto) {
        Booking existingBooking = repository.findById(dto.getId())
                .orElseThrow(() -> new BookingNotFoundException(dto.getId()));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        if (existingBooking.getStatus().equals(BookingStatus.CANCELLED) && !dto.getStatus().equals(BookingStatus.CANCELLED)) {
            //check for existing bookings where status is not 'CANCELLED' on the same dates and return error if found
            List<Booking> existingBookings = repository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BookingStatus.CANCELLED);

            if (!existingBookings.isEmpty()) {
                throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
            }

            //check for existing blocks where status is not 'CANCELLED' on the same dates and return error if found
            List<Block> existingBlocks = blockRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, dto.getEndDate(), dto.getStartDate(), BlockStatus.CANCELLED);

            if (!existingBlocks.isEmpty()) {
                throw new PropertyNotAvailableException(property.getId(), property.getName(), dto.getStartDate(), dto.getEndDate());
            }
        }

        existingBooking.setStartDate(dto.getStartDate());
        existingBooking.setEndDate(dto.getEndDate());
        existingBooking.setStatus(dto.getStatus());
        Booking updatedBooking = repository.save(existingBooking);

        return bookingMapper.bookingToBookingDTO(updatedBooking);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
