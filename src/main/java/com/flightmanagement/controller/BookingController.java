package com.flightmanagement.controller;

import com.flightmanagement.dto.ApiResponse;
import com.flightmanagement.dto.BookingRequest;
import com.flightmanagement.dto.BookingResponse;
import com.flightmanagement.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    // Book Flight — USER only
    @PostMapping
    public ResponseEntity<ApiResponse> bookFlight(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        BookingResponse booking = bookingService.bookFlight(
                userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Flight booked successfully!", booking));
    }

    // Get My Bookings — USER
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        List<BookingResponse> bookings = bookingService.getMyBookings(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Bookings fetched successfully!", bookings));
    }

    // Get All Bookings — ADMIN
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(new ApiResponse(true, "All bookings fetched!", bookings));
    }

    // Get Booking By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Booking fetched successfully!", booking));
    }

    // Cancel Booking — USER
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse booking = bookingService.cancelBooking(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled successfully!", booking));
    }
}
