package com.flightmanagement.model;

import com.flightmanagement.model.enums.FlightStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "flights")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String flightName;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate flightDate;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private LocalTime arrivalTime;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FlightStatus status;

}

