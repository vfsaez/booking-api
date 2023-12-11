package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.repositories.BlockRepository;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.services.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PropertyServiceTest {

    @InjectMocks
    private PropertyService propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BlockRepository blockRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckPropertyAvailabilityOnPeriod_NoError() {
        Property property = new Property();
        property.setId(1L);
        Date startDate = new Date();
        Date endDate = new Date();

        when(bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, endDate, startDate, BookingStatus.CANCELLED))
                .thenReturn(Collections.emptyList());

        when(blockRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, endDate, startDate, BlockStatus.CANCELLED))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> propertyService.checkPropertyAvailabilityOnPeriod(property, startDate, endDate));
    }

    @Test
    public void testCheckPropertyAvailabilityOnPeriod_ErrorExpected() {
        Property property = new Property();
        property.setId(1L);
        Date startDate = new Date();
        Date endDate = new Date();

        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, endDate, startDate, BookingStatus.CANCELLED))
                .thenReturn(Collections.singletonList(booking));

        assertThrows(PropertyNotAvailableException.class, () -> propertyService.checkPropertyAvailabilityOnPeriod(property, startDate, endDate));
    }

    @Test
    public void testFindAll() {
        Property property = new Property();
        property.setId(1L);
        // set other fields as necessary
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        when(propertyRepository.findAll()).thenReturn((List<Property>) Collections.singletonList(property));

        List<PropertyDTO> properties = propertyService.findAll(mockUserDetails);

        assertEquals(1, properties.size());
        assertEquals(1L, properties.get(0).getId());
    }
}