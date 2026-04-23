package com.flightmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Seats is required")
    @Min(value = 1, message = "Minimum 1 seat required")
    private Integer seatsBooked;
}
