package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAllUsers() {
        User user = new User();
        user.setId(1L);
        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        CustomSpringUser mockUserDetails = Mockito.mock(CustomSpringUser.class);
        when(mockUserDetails.isAdmin()).thenReturn(true);
        Page<UserDTO> users = userService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, users.getTotalElements());
        assertEquals(1L, users.getContent().get(0).getId());
    }
}