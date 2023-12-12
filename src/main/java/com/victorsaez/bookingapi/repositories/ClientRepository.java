package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findAllByOwnerId(Pageable pageable, Long ownerId);
}
