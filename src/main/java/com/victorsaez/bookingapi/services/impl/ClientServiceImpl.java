package com.victorsaez.bookingapi.services.impl;

import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.exceptions.ClientNotFoundException;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.services.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    public ClientRepository repository;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClientDTO> findAll() {
        List<Client> list = repository.findAll();
        return list.stream().map(ClientDTO::new).collect(Collectors.toList());
    }

    @Override
    public ClientDTO findById(Long id) throws ClientNotFoundException {
        return new ClientDTO(repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id)));
    }

    @Override
    public ClientDTO insert(ClientDTO dto) {
        Client clientToSave = repository.save(new Client(dto));
        return new ClientDTO(clientToSave);
    }

    @Override
    public ClientDTO update(ClientDTO dto) {
        Client existingClient = repository.findById(dto.getId())
                .orElseThrow(() -> new ClientNotFoundException(dto.getId()));

        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());

        Client updatedClient = repository.save(existingClient);

        return new ClientDTO(updatedClient);
    }

    @Override
    public void delete(Long id) {
        this.findById(id);
        repository.deleteById(id);
    }
}
