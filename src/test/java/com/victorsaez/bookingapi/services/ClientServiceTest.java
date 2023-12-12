package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.repositories.ClientRepository;
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

public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAllClients() {
        Client client = new Client();
        client.setId(1L);
        List<Client> clientList = Collections.singletonList(client);
        Page<Client> clientPage = new PageImpl<>(clientList);

        when(clientRepository.findAll(any(Pageable.class))).thenReturn(clientPage);

        CustomSpringUser mockUserDetails = Mockito.mock(CustomSpringUser.class);
        when(mockUserDetails.isAdmin()).thenReturn(true);
        Page<ClientDTO> clients = clientService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, clients.getTotalElements());
        assertEquals(1L, clients.getContent().get(0).getId());
    }
}