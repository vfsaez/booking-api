package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIsNot(Property property, Date endDate, Date startDate, BlockStatus status);
    Page<Block> findAllByOwnerId(Long ownerId, Pageable pageable);

}
