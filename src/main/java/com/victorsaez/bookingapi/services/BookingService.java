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

    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
    private static final Logger logger = LogManager.getLogger(BookingService.class);
    public BookingService(BookingRepository repository, ClientRepository clientRepository, PropertyRepository propertyRepository, PropertyService propertyService) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
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

    public BookingDTO insert(BookingDTO bookingDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Client client = clientRepository.findById(bookingDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(bookingDto.getClientId()));
        Property property = propertyRepository.findById(bookingDto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingDto.getPropertyId()));
        propertyService.checkPropertyAvailabilityOnPeriod(property, bookingDto.getStartDate(), bookingDto.getEndDate());

        Booking booking = bookingMapper.bookingDTOtoBooking(bookingDto);
        booking.setClient(client);
        booking.setProperty(property);
        booking.setPrice(property.getPrice());
        booking.setOwner(customCurrentUserDetails.getUser());
        Booking createdBooking = repository.save(booking);

        logger.info("user {} Booking id {} created for property id {} and client id {}", customCurrentUserDetails.getId(), createdBooking.getId(), createdBooking.getProperty().getId(), createdBooking.getClient().getId());
        return bookingMapper.bookingToBookingDTO(createdBooking);
    }

    public BookingDTO patch(Long id, BookingDTO bookingDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Booking existingBooking = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (bookingDto.getStartDate() != null) {
            existingBooking.setStartDate(bookingDto.getStartDate());
        }
        if (bookingDto.getEndDate() != null) {
            existingBooking.setEndDate(bookingDto.getEndDate());
        }
        if (bookingDto.getStatus() != null) {
            existingBooking.setStatus(bookingDto.getStatus());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingBooking.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(id, customCurrentUserDetails.getId());
        }

        Booking updatedBooking = repository.save(existingBooking);
        logger.info("user {} Booking id {} patched for property id {} and client id {}", customCurrentUserDetails.getId(), updatedBooking.getId(), updatedBooking.getProperty().getId(), updatedBooking.getClient().getId());
        return bookingMapper.bookingToBookingDTO(updatedBooking);
    }

    public BookingDTO update(BookingDTO bookingDto, UserDetails currentUserDetails) throws AccessDeniedException, PropertyNotAvailableException, PropertyNotFoundException, BookingNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Booking existingBooking = repository.findById(bookingDto.getId())
                .orElseThrow(() -> new BookingNotFoundException(bookingDto.getId()));

        Property property = propertyRepository.findById(bookingDto.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException(bookingDto.getPropertyId()));

        if (existingBooking.getStatus().equals(BookingStatus.CANCELLED) && !bookingDto.getStatus().equals(BookingStatus.CANCELLED)) {
            propertyService.checkPropertyAvailabilityOnPeriod(property, bookingDto.getStartDate(), bookingDto.getEndDate());
        }

        existingBooking.setStartDate(bookingDto.getStartDate());
        existingBooking.setEndDate(bookingDto.getEndDate());
        existingBooking.setStatus(bookingDto.getStatus());

        if (!customCurrentUserDetails.isAdmin()
                && !existingBooking.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(bookingDto.getId(), customCurrentUserDetails.getId());
        }

        Booking updatedBooking = repository.save(existingBooking);
        logger.info("user {} Booking id {} updated for property id {} and client id {}", customCurrentUserDetails.getId(), updatedBooking.getId(), updatedBooking.getProperty().getId(), updatedBooking.getClient().getId());
        return bookingMapper.bookingToBookingDTO(updatedBooking);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BookingDTO bookingDto = this.findById(id, currentUserDetails);
        logger.info("user {} Booking id {} deleted for property id {} and client id {}", customCurrentUserDetails.getId(), bookingDto.getId(), bookingDto.getPropertyId(), bookingDto.getClientId());
        repository.deleteById(id);
    }
}
