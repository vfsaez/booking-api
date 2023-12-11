package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.enums.BlockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockDTO {

    private Long id;
    private Date startDate;
    private Date endDate;
    private BlockStatus status;
    private ClientDTO client;
    private Double price;
    private PropertyDTO property;

    public Long getClientId() {
        return client.getId();
    }

    public Long getPropertyId() {
        return property.getId();
    }
}

