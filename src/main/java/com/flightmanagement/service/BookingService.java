package com.flightmanagement.service;

import com.flightmanagement.dto.BookingRequest;
import com.flightmanagement.dto.BookingResponse;
import com.flightmanagement.exception.BookingNotFoundException;
import com.flightmanagement.exception.FlightNotFoundException;
import com.flightmanagement.model.Booking;
import com.flightmanagement.model.Flight;
import com.flightmanagement.model.User;
import com.flightmanagement.model.enums.BookingStatus;
import com.flightmanagement.repository.BookingRepository;
import com.flightmanagement.repository.FlightRespository;
import com.flightmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRespository flightRespository;

    @Autowired
    private UserRepository userRepository;

    // Book Flight — USER only
    @Transactional
    public BookingResponse bookFlight(
            String email, BookingRequest request) {
        // fetch User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // fetch Flight
        Flight flight = flightRespository
                .findById(request.getFlightId())
                .orElseThrow(() -> new FlightNotFoundException("Flight not found!"));

        // Flight active hai?
        if (!flight.getStatus().name().equals("ACTIVE")) {
            throw new RuntimeException("Flight is not active!");
        }

        // Seats available hain?
        if (flight.getAvailableSeats() < request.getSeatsBooked()) {
            throw new RuntimeException(
                    "Not enough seats available! " +
                            "Available: " +
                            flight.getAvailableSeats());
        }

        // Total amount calculate karo
        Double totalAmount = flight.getPrice() *
                request.getSeatsBooked();

        // Booking banao
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setSeatsBooked(request.getSeatsBooked());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        // Available seats kam karna
        flight.setAvailableSeats(flight.getAvailableSeats() - request.getSeatsBooked());
        flightRespository.save(flight);

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    // Get My Bookings — USER
    public List<BookingResponse> getMyBookings(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return bookingRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get All Bookings — ADMIN
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Booking By ID
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository
                .findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    // Cancel Booking — USER
    @Transactional
    public BookingResponse cancelBooking(Long id, String email) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        // Check karna booking us user ki hai?
        if (!booking.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only cancel your own booking!");
        }

        // Already cancelled?
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled!");
        }

        // Seats wapas karo
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatsBooked());
        flightRespository.save(flight);

        // Status update karo
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    // Entity to Response mapper
    public BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserName(booking.getUser().getName());
        response.setFlightId(booking.getFlight().getId());
        response.setFlightNumber(booking.getFlight().getFlightNumber());
        response.setFlightName(booking.getFlight().getFlightName());
        response.setSource(booking.getFlight().getSource());
        response.setDestination(booking.getFlight().getDestination());
        response.setSeatsBooked(booking.getSeatsBooked());
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus().name());
        response.setBookingTime(booking.getBookingTime());
        return response;
    }

}
