package com.flightmanagement.controller;


import com.flightmanagement.dto.ApiResponse;
import com.flightmanagement.dto.FlightRequest;
import com.flightmanagement.dto.FlightResponse;
import com.flightmanagement.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

    // Add Flight — ADMIN only
    @PostMapping
    public ResponseEntity<ApiResponse> addFlight(@Valid @RequestBody FlightRequest request) {
        FlightResponse flight = flightService.addFlight(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Flight added successfully!", flight));
    }

    // Get All Flights — USER + ADMIN
    @GetMapping
    public ResponseEntity<ApiResponse> getAllFlights() {
        List<FlightResponse> flights = flightService.getAllFlights();
        return ResponseEntity.ok(new ApiResponse(true, "Flights fetched successfully!", flights));
    }

    // Get Flight By ID — USER + ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFlightById(@PathVariable Long id) {
        FlightResponse flight = flightService.getFlightById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Flight fetched successfully!", flight));
    }

    // Search Flights — USER + ADMIN
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchFlights(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FlightResponse> flights = flightService.searchFlights(source, destination, date);
        return ResponseEntity.ok(new ApiResponse(true, "Flights found!", flights));
    }

    // Update Flight — ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFlight(@PathVariable Long id, @Valid @RequestBody FlightRequest request) {
        FlightResponse flight = flightService.updateFlight(id, request);
        return ResponseEntity.ok(new ApiResponse(true, "Flight updated successfully!", flight));
    }

    // Delete Flight — ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFlight(@PathVariable Long id) {
        String message = flightService.deleteFlight(id);
        return ResponseEntity.ok(new ApiResponse(true, message, null));
    }

    // Cancel Flight — ADMIN only
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelFlight(@PathVariable Long id) {
        FlightResponse flight = flightService.cancelFlight(id);
        return ResponseEntity.ok(new ApiResponse(true, "Flight cancelled successfully!", flight));
    }
}
