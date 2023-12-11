package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);
    User userDTOtoUser(UserDTO userDTO);
}