package com.victorsaez.bookingapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;

    @NotNull(message = "startDate can't be null or empty")
    private Date startDate;
    @NotNull(message = "startDate can't be null or empty")
    private Date endDate;

    @NotNull(message = "status can't be null or empty")
    private BookingStatus status;
    private Double price;

    private Long clientId;
    private ClientDTO client;


    private Long propertyId;
    private PropertyDTO property;

    @NotNull(message = "propertyId can't be null or empty")
    public Long getPropertyId() {
        return property != null ? property.getId() : propertyId;
    }

    @NotNull(message = "clientId can't be null or empty")
    public Long getClientId() {
        return client != null ? client.getId() : clientId;
    }
}

