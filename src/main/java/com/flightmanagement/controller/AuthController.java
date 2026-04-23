package com.flightmanagement.controller;


import com.flightmanagement.dto.ApiResponse;
import com.flightmanagement.dto.LoginRequest;
import com.flightmanagement.dto.RegisterRequest;
import com.flightmanagement.model.User;
import com.flightmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully!", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "Login successful!", token));
    }
}
