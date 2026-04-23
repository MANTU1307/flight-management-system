package com.flightmanagement.service;

import com.flightmanagement.dto.FlightRequest;
import com.flightmanagement.dto.FlightResponse;
import com.flightmanagement.exception.FlightNotFoundException;
import com.flightmanagement.model.Flight;
import com.flightmanagement.model.enums.FlightStatus;
import com.flightmanagement.repository.FlightRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightService {

    @Autowired
    private FlightRespository flightRespository;

    // Add Flight — ADMIN only
    public FlightResponse addFlight(FlightRequest request) {
        // Duplicate flight number check
        if (flightRespository.existsByFlightNumber(request.getFlightNumber())) {
            throw new RuntimeException("Flight number already exists!");
        }

        // Source aur destination same nahi hone chahiye
        if (request.getSource().equalsIgnoreCase(request.getDestination())) {
            throw new RuntimeException("Source and destination cannot be same!");
        }
        Flight flight = new Flight();
        flight.setFlightNumber(request.getFlightNumber());
        flight.setFlightName(request.getFlightName());
        flight.setSource(request.getSource());
        flight.setDestination(request.getDestination());
        flight.setFlightDate(request.getFlightDate());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setPrice(request.getPrice());
        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(request.getTotalSeats());
        flight.setStatus(FlightStatus.ACTIVE);

        Flight saved = flightRespository.save(flight);
        return mapToResponse(saved);
    }

    // Get All Flights
    public List<FlightResponse> getAllFlights() {
        return flightRespository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Flight By ID
    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRespository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + id));
        return mapToResponse(flight);
    }

    // Search Flights
    public List<FlightResponse> searchFlights(
            String source,
            String destination,
            LocalDate date) {

        List<Flight> flights = flightRespository.searchAvailableFlights(source, destination, date);
        if (flights.isEmpty()) {
            throw new FlightNotFoundException("No flights available for this route!");
        }
        return flights.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update Flight — ADMIN only
    public FlightResponse updateFlight(Long id, FlightRequest request) {

        Flight flight = flightRespository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + id));
        if (request.getSource().equalsIgnoreCase(
                request.getDestination())) {
            throw new RuntimeException(
                    "Source and destination cannot be same!");
        }

        flight.setFlightNumber(request.getFlightNumber());
        flight.setFlightName(request.getFlightName());
        flight.setSource(request.getSource());
        flight.setDestination(request.getDestination());
        flight.setFlightDate(request.getFlightDate());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setPrice(request.getPrice());
        // ✅ totalSeats update karo
        // Available seats bhi update karo
        int oldTotalSeats = flight.getTotalSeats();
        int oldAvailableSeats = flight.getAvailableSeats();
        int bookedSeats = oldTotalSeats - oldAvailableSeats;
        // Naye totalSeats se booked seats minus karo
        int newAvailableSeats = request.getTotalSeats() - bookedSeats;

        // Available seats negative nahi honi chahiye
        if (newAvailableSeats < 0) {
            throw new RuntimeException("Total seats cannot be less than booked seats: " + bookedSeats);
        }

        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(newAvailableSeats);

        Flight updated = flightRespository.save(flight);
        return mapToResponse(updated);
    }

    // Delete Flight — ADMIN only
    public String deleteFlight(Long id) {
        Flight flight = flightRespository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + id));
        flightRespository.delete(flight);
        return "Flight deleted successfully!";
    }

    // Cancel Flight — ADMIN only
    public FlightResponse cancelFlight(Long id) {
        Flight flight = flightRespository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + id));
        flight.setStatus(FlightStatus.CANCELLED);
        Flight updated = flightRespository.save(flight);
        return mapToResponse(updated);
    }

    private FlightResponse mapToResponse(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setFlightName(flight.getFlightName());
        response.setSource(flight.getSource());
        response.setDestination(flight.getDestination());
        response.setFlightDate(flight.getFlightDate());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setPrice(flight.getPrice());
        response.setTotalSeats(flight.getTotalSeats());
        response.setAvailableSeats(flight.getAvailableSeats());
        response.setStatus(flight.getStatus().name());
        return response;
    }

}
