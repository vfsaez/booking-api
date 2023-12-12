package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Page<Property> findAllByOwnerId(Long ownerId, Pageable pageable);

}
