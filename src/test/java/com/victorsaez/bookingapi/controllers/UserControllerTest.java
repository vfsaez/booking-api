package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
        userDTO.setName("Test User");

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<UserDTO> userList = Collections.singletonList(userDTO);
        Page<UserDTO> userPage = new PageImpl<>(userList);

        Mockito.when(userService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(userPage);
        Mockito.when(userService.findById(anyLong(), any(UserDetails.class))).thenReturn(userDTO);
        Mockito.when(userService.insert(any(UserDTO.class), any(UserDetails.class))).thenReturn(userDTO);
        Mockito.when(userService.update(any(UserDTO.class), any(UserDetails.class))).thenReturn(userDTO);
        Mockito.doNothing().when(userService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/blocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        UserDTO newUser = new UserDTO();
        newUser.setId(1L);
        newUser.setUsername("testUser");
        newUser.setName("Test User");
        newUser.setPassword("testPassword");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));


        mockMvc.perform(post("/users")
                        .with(user("testAdmin").roles("ADMIN")) // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1L);
        updatedUser.setUsername("testUser");
        updatedUser.setName("Test User");
        updatedUser.setPassword("testPassword");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));



        mockMvc.perform(put("/users/{id}", 1L)
                        .with(user("testAdmin").roles("ADMIN"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}