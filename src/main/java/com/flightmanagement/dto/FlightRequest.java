package com.flightmanagement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FlightRequest {
    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotBlank(message = "Flight name is required")
    private String flightName;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Flight date is required")
    @Future(message = "Flight date must be in future")
    private LocalDate flightDate;

    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Seats must be greater than 0")
    private Integer totalSeats;
}
