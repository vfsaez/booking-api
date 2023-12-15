package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.enums.BookingStatus;
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

import java.util.Calendar;
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
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @BeforeEach
    public void setup() {
        ClientDTO clientDto = new ClientDTO();
        clientDto.setId(1L);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<ClientDTO> clientList = Collections.singletonList(clientDto);
        Page<ClientDTO> clientPage = new PageImpl<>(clientList);

        when(clientService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(clientPage);
        when(clientService.findById(anyLong(), any(UserDetails.class))).thenReturn(clientDto);
        when(clientService.insert(any(ClientDTO.class), any(UserDetails.class))).thenReturn(clientDto);
        when(clientService.update(any(ClientDTO.class), any(UserDetails.class))).thenReturn(clientDto);
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
    public void shouldPatchClient() throws Exception {
        ClientDTO patchedClient = new ClientDTO();
        patchedClient.setId(1L);
        patchedClient.setName("Patched Client");

        when(clientService.patch(anyLong(), any(ClientDTO.class), any(UserDetails.class))).thenReturn(patchedClient);

        mockMvc.perform(patch("/v1/clients/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedClient)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Patched Client\"}"));
    }
    @Test
    public void shouldCreateNewClient() throws Exception {
        ClientDTO newClient = new ClientDTO();
        newClient.setId(1L);
        newClient.setName("testClient");

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
        updatedClient.setName("testClient");

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