package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
