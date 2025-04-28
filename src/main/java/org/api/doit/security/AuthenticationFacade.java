package org.api.doit.security;

import org.api.doit.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationFacade {

    /**
     * Retrieves the currently authenticated user from the security context.
     * Throws UnauthorizedException if no authenticated user is found.
     *
     * @return the CustomUserDetails object of the authenticated user.
     * @throws UnauthorizedException if no user is authenticated.
     */
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is valid, the user is authenticated,
        // and the principal is of type CustomUserDetails.
        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails; // Return the custom user details if valid.
        }

        // If the user is not authenticated, throw an UnauthorizedException.
        throw new UnauthorizedException("No authenticated user found");
    }

    /**
     * Retrieves the username of the currently authenticated user.
     * Uses the getCurrentUser method to fetch the user.
     *
     * @return the username of the authenticated user.
     * @throws UnauthorizedException if no user is authenticated.
     */
    public String getUsername() throws UnauthorizedException {
        return getCurrentUser().getUsername(); // Calls getCurrentUser() to retrieve the username.
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     * Uses the getCurrentUser method to fetch the user.
     *
     * @return the UUID of the authenticated user.
     * @throws UnauthorizedException if no user is authenticated.
     */
    public UUID getId() throws UnauthorizedException {
        return getCurrentUser().getId(); // Calls getCurrentUser() to retrieve the user ID.
    }
}
