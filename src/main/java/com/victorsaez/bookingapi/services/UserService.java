package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.dto.requests.SignupRequest;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
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

    public UserDTO insert(UserDTO userDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(hashedPassword);

        if (repository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UsernameNotAvailableException();
        }

        if (customCurrentUserDetails != null && !customCurrentUserDetails.isAdmin()){
            throw new AccessDeniedException(customCurrentUserDetails.getId());
        }

        var userSaved = repository.save(userMapper.userDTOtoUser(userDto));

        return userMapper.userToUserDTO(userSaved);
    }

    public UserDTO register(SignupRequest signupRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        signupRequest.setPassword(hashedPassword);

        UserDTO userDto = new UserDTO();
        userDto.setUsername(signupRequest.getUsername());
        userDto.setPassword(signupRequest.getPassword());
        userDto.setName(signupRequest.getName());
        userDto.setRoles("USER");

        return this.insert(userDto, null);
    }

    public UserDTO update(UserDTO userDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        User existingUser = repository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException(userDto.getId()));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(hashedPassword);

        existingUser.setUsername(userDto.getUsername());
        existingUser.setPassword(userDto.getPassword());
        existingUser.setName(userDto.getName());

        if(customCurrentUserDetails.isAdmin()) {
            existingUser.setRoles(userDto.getRoles());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingUser.getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(userDto.getId(), customCurrentUserDetails.getId());
        }
        User updatedUser = repository.save(existingUser);

        return userMapper.userToUserDTO((updatedUser));
    }

    public UserDTO patch(Long id, UserDTO userDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userDto.getUsername() != null) {
            existingUser.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(hashedPassword);
        }
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        if(customCurrentUserDetails.isAdmin() && userDto.getRoles() != null) {
            existingUser.setRoles(userDto.getRoles());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingUser.getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(userDto.getId(), customCurrentUserDetails.getId());
        }

        User updatedUser = repository.save(existingUser);
        logger.info("user {} User id {} patched", customCurrentUserDetails.getId(), updatedUser.getId());
        return userMapper.userToUserDTO(updatedUser);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        UserDTO userDto = this.findById(id, currentUserDetails);
        logger.info("user {} User id {} deleted", customCurrentUserDetails.getId(), userDto.getId());
        repository.deleteById(id);
    }
}
