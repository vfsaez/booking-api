package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.mappers.PropertyMapper;
import com.victorsaez.bookingapi.repositories.BlockRepository;
import com.victorsaez.bookingapi.repositories.BookingRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    public PropertyRepository repository;

    private final BookingRepository bookingRepository;

    private final BlockRepository blockRepository;

    private final PropertyMapper propertyMapper = PropertyMapper.INSTANCE;

    public PropertyService(PropertyRepository repository, BookingRepository bookingRepository, BlockRepository blockRepository) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.blockRepository = blockRepository;
    }

    public List<PropertyDTO> findAll(UserDetails currentUserDetails) {
        List<Property> properties = repository.findAll();
        return properties.stream()
                .map(propertyMapper::propertyToPropertyDTO)
                .collect(Collectors.toList());
    }

    public PropertyDTO findById(Long id, UserDetails currentUserDetails) throws PropertyNotFoundException {
        return propertyMapper.propertyToPropertyDTO(repository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id)));
    }

    public PropertyDTO insert(PropertyDTO dto, UserDetails currentUserDetails) {
        var propertySaved = repository.save(propertyMapper.propertyDTOtoProperty(dto));
        return propertyMapper.propertyToPropertyDTO(propertySaved);
    }

    public void checkPropertyAvailabilityOnPeriod(Property property, Date startDate, Date endDate) {
        //check for existing bookings where status is not 'CANCELLED' on the same dates and return error if found
        List<Booking> existingBookings = bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, endDate, startDate, BookingStatus.CANCELLED);

        if (!existingBookings.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), startDate, endDate);
        }

        //check for existing blocks where status is not 'CANCELLED' on the same dates and return error if found
        List<Block> existingBlocks = blockRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(property, endDate, startDate, BlockStatus.CANCELLED);

        if (!existingBlocks.isEmpty()) {
            throw new PropertyNotAvailableException(property.getId(), property.getName(), startDate, endDate);
        }
    }

    public PropertyDTO update(PropertyDTO dto, UserDetails currentUserDetails) {
        Property existingProperty = repository.findById(dto.getId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getId()));

        existingProperty.setName(dto.getName());
        existingProperty.setPrice(dto.getPrice());

        Property updatedProperty = repository.save(existingProperty);

        return propertyMapper.propertyToPropertyDTO(updatedProperty);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        this.findById(id, currentUserDetails);
        repository.deleteById(id);
    }
}
