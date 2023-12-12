package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.UserNotFoundException;
import com.victorsaez.bookingapi.mappers.UserMapper;
import com.victorsaez.bookingapi.repositories.UserRepository;
import com.victorsaez.bookingapi.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    public UserRepository repository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Page<UserDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        Page<User> users = repository.findAll(pageable);
        return users.map(userMapper::userToUserDTO);
    }

    public UserDTO findById(Long id, UserDetails currentUserDetails) throws UserNotFoundException {
        return userMapper.userToUserDTO(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    public UserDTO insert(UserDTO dto, UserDetails currentUserDetails) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("user");
        dto.setPassword(hashedPassword);
        var userSaved = repository.save(userMapper.userDTOtoUser(dto));
        return userMapper.userToUserDTO(userSaved);
    }

    public UserDTO update(UserDTO dto, UserDetails currentUserDetails) {
        CustomSpringUser customCurrentUserDetails = (CustomSpringUser) currentUserDetails;
        User existingUser = repository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));

        existingUser.setUsername(dto.getUsername());
        existingUser.setPassword(dto.getPassword());
        existingUser.setName(dto.getName());
        if (customCurrentUserDetails.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
                && !existingUser.getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }
        User updatedUser = repository.save(existingUser);

        return userMapper.userToUserDTO((updatedUser));
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        this.findById(id, currentUserDetails);
        repository.deleteById(id);
    }
}
