package com.victorsaez.bookingapi.services;


import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Property;

import java.util.Date;
import java.util.List;

public interface PropertyService {
     List<PropertyDTO> findAll();

    PropertyDTO findById(Long id);
    
    PropertyDTO insert(PropertyDTO dto);

    PropertyDTO update(PropertyDTO dto);

    void delete(Long id);

    void checkPropertyAvailabilityOnPeriod(Property property, Date startDate, Date endDate);

}

