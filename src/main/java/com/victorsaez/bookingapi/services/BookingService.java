package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.*;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.*;
import com.victorsaez.bookingapi.mappers.BookingMapper;
import com.victorsaez.bookingapi.repositories.*;


import com.victorsaez.bookingapi.services.PropertyService;
import com.victorsaez.bookingapi.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final ClientRepository clientRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;

    private final UserRepository userRepository;
    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private static final Logger logger = LogManager.getLogger(BookingService.class);
    public BookingService(BookingRepository repository, ClientRepository clientRepository, PropertyRepository propertyRepository, PropertyService propertyService, UserRepository userRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
        this.userRepository = userRepository;
    }

    public Page<BookingDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) currentUserDetails;
        Page<Booking> bookings = customUserDetails.isAdmin() ?
                repository.findAll(pageable):
                repository.findAllByOwnerId(customUserDetails.getId(), pageable);
        return bookings.map(bookingMapper::bookingToBookingDTO);
    }

    public BookingDTO findById(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return bookingMapper.bookingToBookingDTO(repository.findById(id).map(booking -> {
            if (customCurrentUserDetails.isAdmin() || booking.getOwner().getId().equals(customCurrentUserDetails.getId())) {
                return booking;
            } else {
                throw new AccessDeniedException(id, customCurrentUserDetails.getId());
            }}).orElseThrow(() -> new BookingNotFoundException(id)));
    }

    public BookingDTO insert(BookingDTO dto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getClientId()));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));
        propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());

        Booking booking = bookingMapper.bookingDTOtoBooking(dto);
        booking.setClient(client);
        booking.setProperty(property);
        booking.setOwner(customCurrentUserDetails.getUser());
        Booking createdBooking = repository.save(booking);

        logger.info("user {} Booking id {} created for property id {} and client id {}", customCurrentUserDetails.getId(), createdBooking.getId(), createdBooking.getProperty().getId(), createdBooking.getClient().getId());
        return bookingMapper.bookingToBookingDTO(createdBooking);
    }


    public BookingDTO update(BookingDTO dto, UserDetails currentUserDetails) throws AccessDeniedException, PropertyNotAvailableException, PropertyNotFoundException, BookingNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Booking existingBooking = repository.findById(dto.getId())
                .orElseThrow(() -> new BookingNotFoundException(dto.getId()));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getPropertyId()));

        if (existingBooking.getStatus().equals(BookingStatus.CANCELLED) && !dto.getStatus().equals(BookingStatus.CANCELLED)) {
            propertyService.checkPropertyAvailabilityOnPeriod(property, dto.getStartDate(), dto.getEndDate());
        }

        existingBooking.setStartDate(dto.getStartDate());
        existingBooking.setEndDate(dto.getEndDate());
        existingBooking.setStatus(dto.getStatus());

        if (!customCurrentUserDetails.isAdmin()
                && !existingBooking.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }

        Booking updatedBooking = repository.save(existingBooking);
        logger.info("user {} Booking id {} updated for property id {} and client id {}", customCurrentUserDetails.getId(), updatedBooking.getId(), updatedBooking.getProperty().getId(), updatedBooking.getClient().getId());
        return bookingMapper.bookingToBookingDTO(updatedBooking);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BookingDTO dto = this.findById(id, currentUserDetails);
        logger.info("user {} Booking id {} deleted for property id {} and client id {}", customCurrentUserDetails.getId(), dto.getId(), dto.getPropertyId(), dto.getClientId());
        repository.deleteById(id);
    }
}
