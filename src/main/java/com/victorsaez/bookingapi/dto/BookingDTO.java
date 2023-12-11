package com.victorsaez.bookingapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Client;
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
    private Date startDate;
    private Date endDate;
    private BookingStatus status;
    private Double price;

    private Long clientId;
    private ClientDTO client;

    private Long propertyId;
    private PropertyDTO property;

    public Long getPropertyId() {
        return property != null ? property.getId() : propertyId;
    }

    public Long getClientId() {
        return client != null ? client.getId() : clientId;
    }
}

