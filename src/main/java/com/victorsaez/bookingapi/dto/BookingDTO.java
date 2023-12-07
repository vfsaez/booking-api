package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;
    private Instant moment;
    private Date startDate;
    private Date endDate;
    private BookingStatus status;
    private ClientDTO client;
    private Double price;
    private PropertyDTO property;

    public BookingDTO(Booking booking) {
        id = booking.getId();
        moment = booking.getMoment();
        startDate = booking.getStartDate();
        endDate = booking.getEndDate();
        status = booking.getStatus();
        client = new ClientDTO(booking.getClient());
        price = booking.getPrice();
        property = new PropertyDTO(booking.getProperty());
    }

    public Long getClientId() {
        return client.getId();
    }

    public Long getPropertyId() {
        return property.getId();
    }
}

