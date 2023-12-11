package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Booking;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public interface BookingService {

    List<BookingDTO> findAll(UserDetails currentUserDetails);

    BookingDTO findById(Long id, UserDetails currentUserDetails);

    BookingDTO insert(BookingDTO booking, UserDetails currentUserDetails);

    BookingDTO update(BookingDTO booking, UserDetails currentUserDetails);

    void delete(Long id, UserDetails currentUserDetails);
}




