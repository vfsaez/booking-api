package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(Property property, Date endDate, Date startDate, BookingStatus status);
    Page<Booking> findAllByOwnerId(Long ownerId, Pageable pageable);

}
