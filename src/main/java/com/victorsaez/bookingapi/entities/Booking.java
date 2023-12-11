package com.victorsaez.bookingapi.entities;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = true)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

}
