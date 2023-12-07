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
    private Instant moment;
    private Date startDate;
    private Date endDate;
    private BlockStatus status;
    private ClientDTO client;
    private Double price;
    private PropertyDTO property;

    public BlockDTO(Block block) {
        id = block.getId();
        moment = block.getMoment();
        startDate = block.getStartDate();
        endDate = block.getEndDate();
        status = block.getStatus();
        client = new ClientDTO(block.getClient());
        price = block.getPrice();
        property = new PropertyDTO(block.getProperty());
    }

    public Long getClientId() {
        return client.getId();
    }

    public Long getPropertyId() {
        return property.getId();
    }
}

