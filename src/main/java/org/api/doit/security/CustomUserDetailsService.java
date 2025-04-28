package org.api.doit.security;

import org.api.doit.entity.User;
import org.api.doit.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor to initialize the CustomUserDetailsService with the UserRepository.
     *
     * @param userRepository the repository used to fetch user data.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username.
     * This method is used by Spring Security to authenticate users.
     * If the user is not found, a UsernameNotFoundException is thrown.
     *
     * @param username the username of the user to be authenticated.
     * @return a UserDetails object containing the user's details.
     * @throws UsernameNotFoundException if the user with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Attempts to find the user by username, and maps the User entity to CustomUserDetails.
        return userRepository.findByUsername(username).map(this::mapToCustomUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // If the user is not found, an exception is thrown.
    }

    /**
     * Loads a user by their UUID (ID).
     * Similar to loadUserByUsername but based on the user's ID.
     *
     * @param id the UUID of the user to be fetched.
     * @return a UserDetails object containing the user's details.
     * @throws UsernameNotFoundException if the user with the given ID is not found.
     */
    public UserDetails loadUserById(UUID id) {
        // Attempts to find the user by ID and maps the User entity to CustomUserDetails.
        return userRepository.findById(id).map(this::mapToCustomUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // If the user is not found, an exception is thrown.
    }

    /**
     * Maps a User entity to a CustomUserDetails object.
     * This method is used to transform a User entity into a UserDetails object
     * that can be used by Spring Security for authentication and authorization.
     *
     * @param user the User entity to be mapped.
     * @return a CustomUserDetails object containing the user's ID, username, and password.
     */
    private CustomUserDetails mapToCustomUserDetails(User user) {
        // Maps the user object to CustomUserDetails, which is used for Spring Security authentication.
        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }
}
