package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.repositories.BookingRepository;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAllBookings() {
        Booking booking = new Booking();
        booking.setId(1L);
        List<Booking> bookingList = Collections.singletonList(booking);
        Page<Booking> bookingPage = new PageImpl<>(bookingList);

        when(bookingRepository.findAll(any(Pageable.class))).thenReturn(bookingPage);

        CustomSpringUser customSpringUser = Mockito.mock(CustomSpringUser.class);
        when(customSpringUser.isAdmin()).thenReturn(true);
        Page<BookingDTO> bookings = bookingService.findAll(Pageable.unpaged(), customSpringUser);

        assertEquals(1, bookings.getTotalElements());
        assertEquals(1L, bookings.getContent().get(0).getId());
    }
}