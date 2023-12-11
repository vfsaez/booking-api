package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.entities.Property;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    PropertyDTO propertyToPropertyDTO(Property property);
    Property propertyDTOtoProperty(PropertyDTO propertyDTO);
}