package com.victorsaez.bookingapi.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String familyName;

    @Column(nullable = false)
    private String email;

    @Column
    private Date dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;
}
