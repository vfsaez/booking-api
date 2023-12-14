package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.dto.requests.SignupRequest;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.exceptions.UserNotFoundException;
import com.victorsaez.bookingapi.exceptions.UsernameNotAvailableException;
import com.victorsaez.bookingapi.mappers.UserMapper;
import com.victorsaez.bookingapi.repositories.UserRepository;
import com.victorsaez.bookingapi.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(UserService.class);


    public Page<UserDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;

        if (!customCurrentUserDetails.isAdmin()) {
            throw new AccessDeniedException(customCurrentUserDetails.getId());
        }

        Page<User> users = repository.findAll(pageable);
        return users.map(userMapper::userToUserDTO);
    }

    public UserDTO findById(Long id, UserDetails currentUserDetails) throws UserNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return userMapper.userToUserDTO(repository.findById(id).map(user -> {
            if (customCurrentUserDetails.isAdmin() || user.getId().equals(((CustomUserDetails) currentUserDetails).getId())) {
                return user;
            } else {
                throw new AccessDeniedException(id, ((CustomUserDetails) currentUserDetails).getId());
            }}).orElseThrow(() -> new UserNotFoundException(id)));
    }

    public UserDTO insert(UserDTO dto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(hashedPassword);

        if (repository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameNotAvailableException();
        }

        if (customCurrentUserDetails != null && !customCurrentUserDetails.isAdmin()){
            throw new AccessDeniedException(customCurrentUserDetails.getId());
        }

        var userSaved = repository.save(userMapper.userDTOtoUser(dto));

        return userMapper.userToUserDTO(userSaved);
    }

    public UserDTO register(SignupRequest signupRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        signupRequest.setPassword(hashedPassword);

        UserDTO dto = new UserDTO();
        dto.setUsername(signupRequest.getUsername());
        dto.setPassword(signupRequest.getPassword());
        dto.setName(signupRequest.getName());
        dto.setRoles("USER");

        return this.insert(dto, null);
    }

    public UserDTO update(UserDTO dto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        User existingUser = repository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(hashedPassword);

        existingUser.setUsername(dto.getUsername());
        existingUser.setPassword(dto.getPassword());
        existingUser.setName(dto.getName());

        if(customCurrentUserDetails.isAdmin()) {
            existingUser.setRoles(dto.getRoles());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingUser.getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }
        User updatedUser = repository.save(existingUser);

        return userMapper.userToUserDTO((updatedUser));
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        UserDTO dto = this.findById(id, currentUserDetails);
        logger.info("user {} User id {} deleted", customCurrentUserDetails.getId(), dto.getId());
        repository.deleteById(id);
    }
}
