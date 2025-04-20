package org.api.doit.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();

        System.out.println("me dispare");

        Map<String, Object> data =
                GlobalExceptionBuilder.build(
                        HttpStatus.BAD_REQUEST.value(),
                        bindingResult.getObjectName(),
                        bindingResult.getFieldError().getDefaultMessage(),
                        request.getRequestURI(),
                        request.getMethod());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
}
