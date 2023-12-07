package com.victorsaez.bookingapi.entities;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tb_property")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;


    public Property(PropertyDTO dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
    }
}
