package com.harikrishnan.finflow.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserAlreadyExistsException (UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ExceptionResponseDto.builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .build());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUsernameNotFoundException (Exception exception) {
        log.error("UsernameNotFoundException: {} " , exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponseDto.builder()
                        .message(exception.getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: {} " , exception.getMessage());
        Map<String , String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.put(fieldError.getField(),fieldError.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponseDto.builder()
                        .errors(fieldErrors)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Error in request parameters.")
                        .build());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleGeneralException (Exception exception) {
        log.error("GeneralException : {} " , exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponseDto.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("A technical error occurred while processing your request.")
                        .build());

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadCredentialsException (Exception exception) {
        log.error("Bad credentials exception : {} " , exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponseDto.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message("Bad credentials found")
                        .build());

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleResourceNotFoundException (Exception exception) {
        log.error("Resource not found exception : {} " , exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ExceptionResponseDto.builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .build());

    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponseDto> handleConflictException (Exception exception) {
        log.error("Conflict exception : {} " , exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponseDto.builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .build());
    }

}
