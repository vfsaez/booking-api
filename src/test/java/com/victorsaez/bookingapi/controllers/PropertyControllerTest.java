package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.services.PropertyService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest
@AutoConfigureMockMvc
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @BeforeEach
    public void setup() {
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setId(1L);
        propertyDTO.setName("Test Property");
        propertyDTO.setPrice(5000.0);
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<PropertyDTO> propertyList = Collections.singletonList(propertyDTO);
        Page<PropertyDTO> propertyPage = new PageImpl<>(propertyList);

        when(propertyService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(propertyPage);
    }

    @Test
    public void shouldReturnAllProperties() throws Exception {
        mockMvc.perform(get("/properties")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1,\"name\":\"Test Property\",\"price\":5000}]}"));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldCreateNewProperty() throws Exception {
        PropertyDTO newProperty = new PropertyDTO();
        newProperty.setId(1L);
        newProperty.setName("Test Property");
        newProperty.setPrice(5000.0);


        when(propertyService.insert(any(PropertyDTO.class), any(UserDetails.class))).thenReturn(newProperty);

        mockMvc.perform(post("/properties")
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newProperty)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test Property\"}"));
    }

    @Test
    public void shouldUpdateProperty() throws Exception {
        PropertyDTO updatedProperty = new PropertyDTO();
        updatedProperty.setId(1L);
        updatedProperty.setName("Updated Property");
        updatedProperty.setPrice(5000.0);

        when(propertyService.update(any(PropertyDTO.class), any(UserDetails.class))).thenReturn(updatedProperty);

        mockMvc.perform(put("/properties/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProperty)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Updated Property\"}"));
    }
}