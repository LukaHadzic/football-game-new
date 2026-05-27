package com.luka.userauth.exception;

import com.luka.userauth.exception.exceptionclasses.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> userAlreadyExists(UserAlreadyExistsException userAlreadyExistsException) {
        return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<String> registrationFailed(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(registrationFailedException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
