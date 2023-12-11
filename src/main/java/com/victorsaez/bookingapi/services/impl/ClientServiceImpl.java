package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.mappers.ClientMapper;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.services.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    public ClientRepository repository;

    private final ClientMapper clientMapper = ClientMapper.INSTANCE;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClientDTO> findAll() {
        List<Client> clients = repository.findAll();
        return clients.stream()
                .map(clientMapper::clientToClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO findById(Long id) throws ClientNotFoundException {
        return clientMapper.clientToClientDTO(repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id)));
    }

    @Override
    public ClientDTO insert(ClientDTO dto) {
        Client clientToSave = repository.save(clientMapper.clientDTOtoClient(dto));
        return clientMapper.clientToClientDTO(clientToSave);
    }

    @Override
    public ClientDTO update(ClientDTO dto) {
        Client existingClient = repository.findById(dto.getId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getId()));

        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());

        Client updatedClient = repository.save(existingClient);

        return clientMapper.clientToClientDTO(updatedClient);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
