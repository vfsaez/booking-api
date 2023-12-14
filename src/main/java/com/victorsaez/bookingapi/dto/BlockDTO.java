package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.enums.BlockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockDTO {

    private Long id;
    @NotNull(message = "startDate can't be null or empty")
    private Date startDate;
    @NotNull(message = "endDate can't be null or empty")
    private Date endDate;
    @NotNull(message = "status can't be null or empty")
    private BlockStatus status;

    private Long propertyId;
    private PropertyDTO property;

    @NotNull(message = "propertyId can't be null or empty")
    public Long getPropertyId() {
        return property != null ? property.getId() : propertyId;
    }

}

