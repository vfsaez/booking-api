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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        PropertyDTO propertyDto = new PropertyDTO();
        propertyDto.setId(1L);
        propertyDto.setName("Test Property");
        propertyDto.setPrice(5000.0);
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<PropertyDTO> propertyList = Collections.singletonList(propertyDto);
        Page<PropertyDTO> propertyPage = new PageImpl<>(propertyList);

        when(propertyService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(propertyPage);
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldPatchProperty() throws Exception {
        PropertyDTO patchedProperty = new PropertyDTO();
        patchedProperty.setId(1L);
        patchedProperty.setName("Patched Property");
        patchedProperty.setPrice(5000.0);

        when(propertyService.patch(anyLong(), any(PropertyDTO.class), any(UserDetails.class))).thenReturn(patchedProperty);

        mockMvc.perform(patch("/v1/properties/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedProperty)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Patched Property\"}"));
    }

    @Test
    public void shouldReturnAllProperties() throws Exception {
        mockMvc.perform(get("/v1/properties")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1,\"name\":\"Test Property\",\"price\":5000}]}"));
    }

    @Test
    public void shouldCreateNewProperty() throws Exception {
        PropertyDTO newProperty = new PropertyDTO();
        newProperty.setId(1L);
        newProperty.setName("Test Property");
        newProperty.setPrice(5000.0);


        when(propertyService.insert(any(PropertyDTO.class), any(UserDetails.class))).thenReturn(newProperty);

        mockMvc.perform(post("/v1/properties")
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

        mockMvc.perform(put("/v1/properties/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedProperty)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Updated Property\"}"));
    }

    @Test
    public void shouldDeleteProperty() throws Exception {
        Mockito.doNothing().when(propertyService).delete(anyLong(), any(UserDetails.class));

        mockMvc.perform(delete("/v1/properties/{id}", 1L)
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isNoContent());
    }
}