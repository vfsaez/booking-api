package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.*;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Client client = new Client();
        client.setId(1L);
        Mockito.when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // Mock a Property
        Property property = new Property();
        property.setId(1L);
        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.of(property));

        Booking bookedBooking = new Booking();
        bookedBooking.setId(1L);
        bookedBooking.setStatus(BookingStatus.BOOKED);
        bookedBooking.setProperty(property);
        bookedBooking.setClient(client);
        Calendar cal = Calendar.getInstance();
        bookedBooking.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        bookedBooking.setEndDate(cal.getTime());


        Booking cancelledBooking = new Booking();
        cancelledBooking.setId(1L);
        cancelledBooking.setStatus(BookingStatus.CANCELLED);
        cancelledBooking.setProperty(property);
        bookedBooking.setClient(client);
        cal = Calendar.getInstance();
        cancelledBooking.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        cancelledBooking.setEndDate(cal.getTime());


        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookedBooking));
        Mockito.when(bookingRepository.findById(2L)).thenReturn(Optional.of(cancelledBooking));

        Mockito.when(bookingRepository.save(any())).thenReturn(bookedBooking);
    }

    @Test
    public void shouldReturnAllBookings() {
        Booking booking = new Booking();
        booking.setId(1L);
        List<Booking> bookingList = Collections.singletonList(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookingList);

        when(bookingRepository.findAll(any(Pageable.class))).thenReturn(bookingPage);

        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        Page<BookingDTO> bookings = bookingService.findAll(Pageable.unpaged(), customUserDetails);

        assertEquals(1, bookings.getTotalElements());
        assertEquals(1L, bookings.getContent().get(0).getId());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBlockWithNonexistentProperty() {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setPropertyId(1L);
        bookingDto.setClientId(1L);

        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(PropertyNotFoundException.class, () -> {
            bookingService.insert(bookingDto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingBookingWithNonexistentProperty() {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setPropertyId(1L);

        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(PropertyNotFoundException.class, () -> {
            bookingService.update(bookingDto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBlockWithUnavailableProperty() {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setPropertyId(1L);
        bookingDto.setClientId(1L);

        Mockito.doThrow(PropertyNotAvailableException.class).when(propertyService).checkPropertyAvailabilityOnPeriod(any(), any(), any());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.isAdmin()).thenReturn(true);
        when(customUserDetails.getId()).thenReturn(1L);
        assertThrows(PropertyNotAvailableException.class, () -> {
            bookingService.insert(bookingDto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingBookingWithUnavailableProperty() {
        BookingDTO updatedBooking = new BookingDTO();
        updatedBooking.setId(2L);
        updatedBooking.setPropertyId(1L);
        updatedBooking.setClientId(1L);
        updatedBooking.setStatus(BookingStatus.BOOKED);


        Mockito.doThrow(PropertyNotAvailableException.class).when(propertyService).checkPropertyAvailabilityOnPeriod(any(), any(), any());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.isAdmin()).thenReturn(true);
        when(customUserDetails.getId()).thenReturn(1L);
        assertThrows(PropertyNotAvailableException.class, () -> {
            bookingService.update(updatedBooking, customUserDetails);
        });
    }
}