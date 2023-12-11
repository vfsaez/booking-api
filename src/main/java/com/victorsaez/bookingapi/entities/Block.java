package com.victorsaez.bookingapi.entities;

import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.enums.BlockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "tb_block")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private BlockStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}
