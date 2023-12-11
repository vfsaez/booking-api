package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.mappers.PropertyMapper;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
import com.victorsaez.bookingapi.services.PropertyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    public PropertyRepository repository;

    private final PropertyMapper propertyMapper = PropertyMapper.INSTANCE;

    public PropertyServiceImpl(PropertyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PropertyDTO> findAll() {
        List<Property> properties = repository.findAll();
        return properties.stream()
                .map(propertyMapper::propertyToPropertyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyDTO findById(Long id) throws PropertyNotFoundException {
        return propertyMapper.propertyToPropertyDTO(repository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id)));
    }

    @Override
    public PropertyDTO insert(PropertyDTO dto) {
        var propertySaved = repository.save(new Property(dto));
        return propertyMapper.propertyToPropertyDTO(propertySaved);
    }

    @Override
    public PropertyDTO update(PropertyDTO dto) {
        Property existingProperty = repository.findById(dto.getId())
                .orElseThrow(() -> new PropertyNotFoundException(dto.getId()));

        existingProperty.setName(dto.getName());
        existingProperty.setPrice(dto.getPrice());

        Property updatedProperty = repository.save(existingProperty);

        return propertyMapper.propertyToPropertyDTO(updatedProperty);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
