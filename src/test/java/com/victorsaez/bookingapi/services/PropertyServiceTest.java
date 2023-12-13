package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void shouldNotThrowExceptionWhenPropertyIsAvailable() {
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
    public void shouldThrowExceptionWhenPropertyIsNotAvailable() {
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
    public void shouldReturnAllProperties() {
        Property property = new Property();
        property.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(true);
        List<Property> propertyList = Collections.singletonList(property);
        Page<Property> propertyPage = new PageImpl<>(propertyList);

        when(propertyRepository.findAll(any(Pageable.class))).thenReturn(propertyPage);

        Page<PropertyDTO> properties = propertyService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, properties.getTotalElements());
        assertEquals(1L, properties.getContent().get(0).getId());
    }

    @Test
    public void shouldReturnAllPropertiesByOwnerId() {
        Property property = new Property();
        property.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);
        List<Property> propertyList = Collections.singletonList(property);
        Page<Property> propertyPage = new PageImpl<>(propertyList);

        when(propertyRepository.findAllByOwnerId(anyLong(),any(Pageable.class))).thenReturn(propertyPage);

        Page<PropertyDTO> properties = propertyService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, properties.getTotalElements());
        assertEquals(1L, properties.getContent().get(0).getId());
    }
}