package org.api.doit.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom entry point for handling authentication errors.
 * This class is responsible for sending a custom error response
 * when an unauthenticated user tries to access a protected resource.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles the error when an authentication exception occurs.
     * It sends a JSON response with the error details, including
     * the status, error message, and other useful information.
     *
     * @param request the HTTP request that caused the authentication exception
     * @param response the HTTP response to be sent back to the client
     * @param authException the exception thrown when authentication fails
     * @throws IOException if an input/output error occurs while writing the response
     * @throws ServletException if a servlet-related error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json"); // Sets response content type to JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set HTTP status to 401 (Unauthorized)

        // Use GlobalExceptionBuilder to format the error response
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpServletResponse.SC_UNAUTHORIZED, // HTTP status code for unauthorized error
                        "Unauthorized", // Error title
                        authException.getMessage(), // Error message from the exception
                        request.getRequestURI(), // URI that was accessed
                        request.getMethod()); // HTTP method used (GET, POST, etc.)

        // Write the error response as a JSON object
        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }
}
