package com.flightmanagement.exception;


import com.flightmanagement.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //     Flight not found — 404
    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ApiResponse> handleFlightNotFound(FlightNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    // Validation errors — 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        false, message, null));
    }

    // Runtime exceptions — 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    // Generic — 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Something went wrong!", null));
    }

    // BookingNotFoundException
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBookingNotFound(BookingNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    // PaymentException
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse> handlePayment(PaymentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }
}
