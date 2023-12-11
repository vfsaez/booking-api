package com.victorsaez.bookingapi.services;


import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Property;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

public interface PropertyService {
     List<PropertyDTO> findAll(UserDetails currentUserDetails);

    PropertyDTO findById(Long id, UserDetails currentUserDetails);
    
    PropertyDTO insert(PropertyDTO dto, UserDetails currentUserDetails);

    PropertyDTO update(PropertyDTO dto, UserDetails currentUserDetails);

    void delete(Long id, UserDetails currentUserDetails);

    void checkPropertyAvailabilityOnPeriod(Property property, Date startDate, Date endDate);

}

