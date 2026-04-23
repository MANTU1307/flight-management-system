package com.flightmanagement.model;

import com.flightmanagement.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(nullable = false)
    private Integer seatsBooked;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime bookingTime;
}
