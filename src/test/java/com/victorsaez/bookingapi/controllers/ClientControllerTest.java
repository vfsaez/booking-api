package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.services.ClientService;
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
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @BeforeEach
    public void setup() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(1L);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<ClientDTO> clientList = Collections.singletonList(clientDTO);
        Page<ClientDTO> clientPage = new PageImpl<>(clientList);

        Mockito.when(clientService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(clientPage);
        Mockito.when(clientService.findById(anyLong(), any(UserDetails.class))).thenReturn(clientDTO);
        Mockito.when(clientService.insert(any(ClientDTO.class), any(UserDetails.class))).thenReturn(clientDTO);
        Mockito.when(clientService.update(any(ClientDTO.class), any(UserDetails.class))).thenReturn(clientDTO);
        Mockito.doNothing().when(clientService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnAllClients() throws Exception {
        mockMvc.perform(get("/v1/clients")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnClientById() throws Exception {
        mockMvc.perform(get("/v1/clients/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewClient() throws Exception {
        ClientDTO newClient = new ClientDTO();
        newClient.setId(1L);

        mockMvc.perform(post("/v1/clients")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newClient)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateClient() throws Exception {
        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setId(1L);

        mockMvc.perform(put("/v1/clients/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedClient)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteClient() throws Exception {
        mockMvc.perform(delete("/v1/clients/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}