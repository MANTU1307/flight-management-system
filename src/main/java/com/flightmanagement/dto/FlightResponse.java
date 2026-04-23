package com.flightmanagement.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String flightName;
    private String source;
    private String destination;
    private LocalDate flightDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    private String status;
}
