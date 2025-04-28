package org.api.doit.service;

import org.api.doit.dto.AuthResponse;
import org.api.doit.dto.LoginRequest;
import org.api.doit.dto.RegisterRequest;
import org.api.doit.entity.User;
import org.api.doit.exception.UserAlreadyExistsException;
import org.api.doit.jwt.JwtService;
import org.api.doit.repository.UserRepository;
import org.api.doit.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling user-related operations,
 * including registration and login with JWT-based authentication.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for accessing user data.
     * @param passwordEncoder Used to securely hash passwords.
     * @param authenticationManager Authenticates user credentials.
     * @param jwtService Service to generate and validate JWT tokens.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user in the system.
     * If the username already exists, throws an exception.
     *
     * @param registerRequest Object containing username and raw password.
     * @return AuthResponse containing a JWT token for the new user.
     */
    @Transactional
    public AuthResponse createUser(RegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.username())) {
            // Prevent duplicate usernames
            throw new UserAlreadyExistsException("Username " + registerRequest.username() + " already exists");
        }

        // Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(registerRequest.password());

        // Persist the new user in the database
        User user = userRepository.save(new User(registerRequest.username(), hashedPassword));

        // Generate JWT token for the new user
        String token = jwtService.generateToken(user.getUsername(), user.getId());

        return new AuthResponse(token);
    }

    /**
     * Authenticates a user and returns a valid JWT token.
     *
     * @param loginRequest Object containing the login credentials.
     * @return AuthResponse containing a JWT token if authentication is successful.
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        final var authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        // Attempt authentication and retrieve the authenticated user
        var authUser = (CustomUserDetails) authenticationManager.authenticate(authToken).getPrincipal();

        // Generate JWT token for the authenticated user
        String token = jwtService.generateToken(authUser.getUsername(), authUser.getId());

        return new AuthResponse(token);
    }
}