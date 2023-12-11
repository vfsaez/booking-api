package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.mappers.ClientMapper;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    public ClientRepository repository;

    private final ClientMapper clientMapper = ClientMapper.INSTANCE;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<ClientDTO> findAll(UserDetails currentUserDetails) {
        List<Client> clients = repository.findAll();
        return clients.stream()
                .map(clientMapper::clientToClientDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO findById(Long id, UserDetails currentUserDetails) throws ClientNotFoundException {
        return clientMapper.clientToClientDTO(repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id)));
    }

    public ClientDTO insert(ClientDTO dto, UserDetails currentUserDetails) {
        Client clientToSave = repository.save(clientMapper.clientDTOtoClient(dto));
        return clientMapper.clientToClientDTO(clientToSave);
    }

    public ClientDTO update(ClientDTO dto, UserDetails currentUserDetails) {
        Client existingClient = repository.findById(dto.getId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getId()));

        existingClient.setName(dto.getName());

        Client updatedClient = repository.save(existingClient);

        return clientMapper.clientToClientDTO(updatedClient);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        this.findById(id, currentUserDetails);
        repository.deleteById(id);
    }
}
