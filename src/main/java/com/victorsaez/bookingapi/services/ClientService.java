package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.mappers.ClientMapper;
import com.victorsaez.bookingapi.repositories.ClientRepository;
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

    public Page<ClientDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomSpringUser customSpringUser = (CustomSpringUser) currentUserDetails;
        Page<Client> clients = customSpringUser.isAdmin() ?
                repository.findAll(pageable):
                repository.findAllByOwnerId(customSpringUser.getId(), pageable);
        return clients.map(clientMapper::clientToClientDTO);
    }

    public ClientDTO findById(Long id, UserDetails currentUserDetails) throws ClientNotFoundException {
        CustomSpringUser customCurrentUserDetails = (CustomSpringUser) currentUserDetails;
        return clientMapper.clientToClientDTO(repository.findById(id).map(client -> {
            if (customCurrentUserDetails.isAdmin() || client.getOwner().getId().equals(((CustomSpringUser) currentUserDetails).getId())) {
                return client;
            } else {
                throw new AccessDeniedException(id, ((CustomSpringUser) currentUserDetails).getId());
            }
        }).orElseThrow(() -> new ClientNotFoundException(id)));
    }

    public ClientDTO insert(ClientDTO dto, UserDetails currentUserDetails) {
        Client clientToSave = clientMapper.clientDTOtoClient(dto);

        clientToSave.setOwner(((CustomSpringUser) currentUserDetails).getUser());
        Client savedClient = repository.save(clientToSave);

        return clientMapper.clientToClientDTO(savedClient);
    }

    public ClientDTO update(ClientDTO dto, UserDetails currentUserDetails) {
        CustomSpringUser customCurrentUserDetails = (CustomSpringUser) currentUserDetails;
        Client existingClient = repository.findById(dto.getId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getId()));

        existingClient.setName(dto.getName());

        if (!customCurrentUserDetails.isAdmin()
                && !existingClient.getOwner().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(dto.getId(), customCurrentUserDetails.getId());
        }

        Client updatedClient = repository.save(existingClient);

        return clientMapper.clientToClientDTO(updatedClient);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        this.findById(id, currentUserDetails);
        repository.deleteById(id);
    }
}
