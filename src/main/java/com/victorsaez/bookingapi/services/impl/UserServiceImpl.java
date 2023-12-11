package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.exceptions.UserNotFoundException;
import com.victorsaez.bookingapi.mappers.UserMapper;
import com.victorsaez.bookingapi.repositories.UserRepository;
import com.victorsaez.bookingapi.services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserRepository repository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDTO> findAll() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Long id) throws UserNotFoundException {
        return new UserDTO(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDTO insert(UserDTO dto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("user");
        dto.setPassword(hashedPassword);
        var propertySaved = repository.save(new User(dto));
        return new UserDTO(propertySaved);
    }

    @Override
    public UserDTO update(UserDTO dto) {
        User existingUser = repository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));

        existingUser.setUsername(dto.getUsername());
        existingUser.setPassword(dto.getPassword());

        User updatedUser = repository.save(existingUser);

        return new UserDTO(updatedUser);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
