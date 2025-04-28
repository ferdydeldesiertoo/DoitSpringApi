package org.api.doit.controller;

import jakarta.validation.Valid;
import org.api.doit.dto.LoginRequest;
import org.api.doit.dto.RegisterRequest;
import org.api.doit.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for authentication operations such as registration and login.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles user registration requests.
     *
     * @param registerRequest the request body containing registration details
     * @return the created user or error response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(registerRequest));
    }

    /**
     * Handles user login requests.
     *
     * @param loginRequest the request body containing login credentials
     * @return the login response including a JWT token if successful
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }
}
