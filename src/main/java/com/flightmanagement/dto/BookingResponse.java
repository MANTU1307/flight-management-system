package com.flightmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long flightId;
    private String flightNumber;
    private String flightName;
    private String source;
    private String destination;
    private Integer seatsBooked;
    private Double totalAmount;
    private String status;
    private LocalDateTime bookingTime;
}
