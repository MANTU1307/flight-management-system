package com.flightmanagement.repository;

import com.flightmanagement.model.Booking;
import com.flightmanagement.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

}
