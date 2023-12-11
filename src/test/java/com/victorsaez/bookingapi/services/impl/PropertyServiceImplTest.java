package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PropertyServiceImplTest {

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        Property property = new Property();
        property.setId(1L);
        // set other fields as necessary

        when(propertyRepository.findAll()).thenReturn((List<Property>) Collections.singletonList(property));

        List<PropertyDTO> properties = propertyService.findAll();

        assertEquals(1, properties.size());
        assertEquals(1L, properties.get(0).getId());
    }
}