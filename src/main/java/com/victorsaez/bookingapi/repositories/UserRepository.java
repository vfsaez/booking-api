package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
