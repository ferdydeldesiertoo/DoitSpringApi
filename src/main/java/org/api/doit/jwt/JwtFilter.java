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
        String authHeader = request.getHeader("Authorization");
        String jwt;
        String id;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                id = jwtService.extractClaim(jwt, "userId", String.class);

                if (jwtService.isTokenValid(jwt) && id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UUID uuid = UUID.fromString(id);
                    UserDetails userDetails = customUserDetailsService.loadUserById(uuid);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
            filterChain.doFilter(request, response);
        } catch(ExpiredJwtException e) {
            customAuthenticationEntryPoint.commence(request, response, new JwtExpiredException("JWT is expired", e));
        } catch (MalformedJwtException | SignatureException e) {
            customAuthenticationEntryPoint.commence(request, response, new JwtInvalidException("Invalid JWT", e));
        }
    }
}

