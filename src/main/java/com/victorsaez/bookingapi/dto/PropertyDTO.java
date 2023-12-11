package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.entities.Property;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PropertyDTO {

    private Long id;
    @NotNull(message = "Name can't be null or empty")
    @Size(min = 3, max = 25)
    private String name;
    @NotNull(message = "Price can't be null or empty")
    @Max(value = 999, message = "Price can't exceed 999.00")
    @Min(value = 5, message = "Price can't be lower than 5")
    private Double price;

}
