package com.flightmanagement.repository;

import com.flightmanagement.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRespository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE " +
            "f.source = :source AND " +
            "f.destination = :destination AND " +
            "f.flightDate = :date AND " +
            "f.availableSeats > 0 AND " +
            "f.status = 'ACTIVE'")
    List<Flight> searchAvailableFlights(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("date") LocalDate date
    );

    boolean existsByFlightNumber(String flightNumber);
}
