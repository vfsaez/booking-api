package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Booking;

import java.util.List;


public interface BookingService {

    List<BookingDTO> findAll();

    BookingDTO findById(Long id);

    BookingDTO insert(BookingDTO booking);

    BookingDTO update(BookingDTO booking);

    void delete(Long id);
}




