package com.victorsaez.bookingapi.entities;

import com.victorsaez.bookingapi.dto.ClientDTO;
import javax.persistence.*;
import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String streetName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String homeNumber;

    @Column(nullable = false)
    private String zipCode;

    public Client(ClientDTO dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.phoneNumber = dto.getPhoneNumber();
        this.streetName = dto.getStreetName();
        this.city = dto.getCity();
        this.homeNumber = dto.getHomeNumber();
        this.zipCode = dto.getZipCode();
    }

    public Client(long id, String name, String email) {
        this.name = name;
        this.email = email;
        this.id = id;
    }
}
