package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.mappers.ClientMapper;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    public ClientRepository repository;

    private final ClientMapper clientMapper = ClientMapper.INSTANCE;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    private static final Logger logger = LogManager.getLogger(ClientService.class);


    public Page<ClientDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) currentUserDetails;
        Page<Client> clients = customUserDetails.isAdmin() ?
                repository.findAll(pageable):
                repository.findAllByOwnerId(customUserDetails.getId(), pageable);
        return clients.map(clientMapper::clientToClientDTO);
    }

    public ClientDTO findById(Long id, UserDetails currentUserDetails) throws ClientNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return clientMapper.clientToClientDTO(repository.findById(id).map(client -> {
            if (customCurrentUserDetails.isAdmin() || client.getOwner().getId().equals(((CustomUserDetails) currentUserDetails).getId())) {
                return client;
            } else {
                throw new AccessDeniedException(id, ((CustomUserDetails) currentUserDetails).getId());
            }
        }).orElseThrow(() -> new ClientNotFoundException(id)));
    }

    public ClientDTO insert(ClientDTO clientDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Client clientToSave = clientMapper.clientDTOtoClient(clientDto);

        clientToSave.setOwner(((CustomUserDetails) currentUserDetails).getUser());
        Client savedClient = repository.save(clientToSave);
        logger.info("user {} Client id {} created", customCurrentUserDetails.getId(), savedClient.getId());
        return clientMapper.clientToClientDTO(savedClient);
    }

    public ClientDTO update(ClientDTO clientDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Client existingClient = repository.findById(clientDto.getId())
                .orElseThrow(() -> new ClientNotFoundException(clientDto.getId()));

        existingClient.setName(clientDto.getName());

        if (!customCurrentUserDetails.isAdmin()
                && !existingClient.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(clientDto.getId(), customCurrentUserDetails.getId());
        }

        Client updatedClient = repository.save(existingClient);
        logger.info("user {} Client id {} updated", customCurrentUserDetails.getId(), updatedClient.getId());
        return clientMapper.clientToClientDTO(updatedClient);
    }

    public ClientDTO patch(Long id, ClientDTO clientDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Client existingClient = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (clientDto.getName() != null) {
            existingClient.setName(clientDto.getName());
        }
        // Add similar checks for other updatable fields

        if (!customCurrentUserDetails.isAdmin()
                && !existingClient.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(id, customCurrentUserDetails.getId());
        }

        Client updatedClient = repository.save(existingClient);
        logger.info("user {} Client id {} patched", customCurrentUserDetails.getId(), updatedClient.getId());
        return clientMapper.clientToClientDTO(updatedClient);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        ClientDTO clientDto = this.findById(id, currentUserDetails);
        logger.info("user {} Client id {} deleted", customCurrentUserDetails.getId(), clientDto.getId());
        repository.deleteById(id);
    }
}
