package org.api.doit.security;

import org.api.doit.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationFacade {
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }

        throw new UnauthorizedException("No authenticated user found");
    }

    public String getUsername() throws UnauthorizedException {
        return getCurrentUser().getUsername();
    }

    public UUID getId() throws UnauthorizedException {
        return getCurrentUser().getId();
    }
}
