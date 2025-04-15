package org.api.doit.service;

import org.api.doit.dto.AuthResponse;
import org.api.doit.dto.LoginRequest;
import org.api.doit.dto.RegisterRequest;
import org.api.doit.entity.User;
import org.api.doit.jwt.JwtService;
import org.api.doit.repository.UserRepository;
import org.api.doit.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void createUser(RegisterRequest registerRequest) {

        String hashedPassword = passwordEncoder.encode(registerRequest.password());

        userRepository.save(new User(registerRequest.username(), hashedPassword));
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        final var authToken = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        var authUser = (CustomUserDetails) authenticationManager.authenticate(authToken).getPrincipal();

        String token = jwtService.generateToken(authUser.getUsername(), authUser.getId());

        return new AuthResponse(token);
    }
}
