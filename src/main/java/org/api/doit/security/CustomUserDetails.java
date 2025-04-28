package org.api.doit.security;

import lombok.Getter;
import org.api.doit.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class is used to wrap the User entity and provide authentication-related information.
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private final UUID id; // Unique identifier for the user.

    private final String username; // The username of the user.

    private final String password; // The user's password.

    /**
     * Constructor to initialize the CustomUserDetails with user data.
     *
     * @param id the unique identifier for the user.
     * @param username the username for authentication.
     * @param password the password for authentication.
     */
    public CustomUserDetails(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the authorities granted to the user.
     * In this implementation, no specific roles or authorities are defined.
     *
     * @return a list of granted authorities, which is empty in this case.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // No authorities are assigned to this user.
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user.
     */
    @Override
    public String getPassword() {
        return password; // Returns the password stored in the object.
    }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user.
     */
    @Override
    public String getUsername() {
        return username; // Returns the username stored in the object.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Always return true, indicating the account is not expired.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Always return true, indicating the account is not locked.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Always return true, indicating the credentials are not expired.
    }

    @Override
    public boolean isEnabled() {
        return true; // Always return true, indicating the account is enabled.
    }
}
