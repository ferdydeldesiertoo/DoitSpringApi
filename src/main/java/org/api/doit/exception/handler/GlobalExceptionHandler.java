package org.api.doit.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.api.doit.exception.TaskNotFoundException;
import org.api.doit.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandler handles various types of exceptions thrown by the application.
 * It formats the exceptions into standardized error responses and returns them to the client.
 * This class is annotated with @RestControllerAdvice, meaning it provides global exception handling for all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException, which is thrown when request parameters fail validation.
     * It formats the validation errors and returns them as a JSON response.
     *
     * @param exception the exception containing validation errors
     * @param request   the HTTP request that caused the exception
     * @return a ResponseEntity with the formatted error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();

        // Extracts validation error details from the BindingResult
        List<Map<String, String>> messages = bindingResult.getFieldErrors().stream().map((error) -> {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("field", error.getField());
            errorMap.put("message", error.getDefaultMessage());
            return errorMap;
        }).toList();

        // Creates a standardized error response
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation error",
                        messages,
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles TaskNotFoundException, which is thrown when a task cannot be found in the database.
     * It returns a 404 Not Found error response with the exception details.
     *
     * @param exception the exception containing the error message
     * @param request   the HTTP request that caused the exception
     * @return a ResponseEntity with the formatted error response
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleTaskNotFoundException(TaskNotFoundException exception, HttpServletRequest request) {
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.NOT_FOUND.value(),
                        "Task not found",
                        exception.getMessage(),
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles UserAlreadyExistsException, which is thrown when trying to create a user that already exists.
     * It returns a 409 Conflict error response with the exception details.
     *
     * @param exception the exception containing the error message
     * @param request   the HTTP request that caused the exception
     * @return a ResponseEntity with the formatted error response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request) {
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.CONFLICT.value(),
                        "User already exists",
                        exception.getMessage(),
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    /**
     * Handles exceptions related to unsupported HTTP methods and route not found errors.
     * Specifically, this method handles {@link NoHandlerFoundException} and
     * {@link HttpRequestMethodNotSupportedException} exceptions by returning a standardized
     * error response with a 404 Not Found status.
     *
     * @param exception the exception that was thrown, either {@link NoHandlerFoundException} or
     *                  {@link HttpRequestMethodNotSupportedException}.
     * @param request   the {@link HttpServletRequest} that triggered the exception, useful for
     *                  capturing the URI and HTTP method.
     * @return a {@link ResponseEntity} containing a map with the error details and a 404 Not Found status.
     */
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<?> handleNoHandlerFoundException(Exception exception, HttpServletRequest request) {
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.NOT_FOUND.value(),
                        "Route not found",
                        exception.getMessage(),
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles HttpMessageNotReadableException, which is thrown when the request body is malformed or incorrectly formatted.
     * It returns a 400 Bad Request error response indicating that the request body is invalid.
     *
     * @param exception the exception containing the error message
     * @param request   the HTTP request that caused the exception
     * @return a ResponseEntity with the formatted error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request) {
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.BAD_REQUEST.value(),
                        "Malformed request body",
                        "The JSON request body is invalid or incorrectly formatted",
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotSupportedException exception, HttpServletRequest request) {
        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.BAD_REQUEST.value(),
                        "Content-Type not supported",
                        exception.getMessage(),
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
}
