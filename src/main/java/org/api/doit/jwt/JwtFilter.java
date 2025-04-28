package org.api.doit.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.api.doit.exception.handler.CustomAuthenticationEntryPoint;
import org.api.doit.exception.JwtExpiredException;
import org.api.doit.exception.JwtInvalidException;
import org.api.doit.security.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that intercepts each request to validate and authenticate a JWT token.
 * If the token is valid, it sets the user details in the SecurityContext.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public JwtFilter(JwtService jwtService,
                     CustomUserDetailsService customUserDetailsService,
                     CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    /**
     * Filters and processes each request to check for a valid JWT token.
     * If valid, it sets authentication in the security context.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            // Checks if the Authorization header is missing or doesn't start with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extracts the token from the header (removes "Bearer ")
            String jwt = authHeader.substring(7);

            // Extracts the userId claim from the token
            String id = jwtService.extractClaim(jwt, "userId", String.class);

            // Validates token and ensures no previous authentication exists
            if (jwtService.isTokenValid(jwt) && id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UUID uuid = UUID.fromString(id); // Parses the string ID into a UUID

                // Loads the UserDetails by user ID
                UserDetails userDetails = customUserDetailsService.loadUserById(uuid);

                // Creates an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Attaches request-specific details to the auth token (e.g., IP, session)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Sets the authentication into the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // Continues the filter chain
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // Token has expired, delegate to custom entry point with specific exception
            customAuthenticationEntryPoint.commence(request, response, new JwtExpiredException("JWT is expired", e));
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // Token is invalid, malformed, or has wrong signature
            customAuthenticationEntryPoint.commence(request, response, new JwtInvalidException("Invalid JWT", e));
        } catch (Exception e) {
            // Any other unexpected error during token processing
            customAuthenticationEntryPoint.commence(request, response, new JwtInvalidException("An error occurred while processing JWT", e));
        }
    }
}
