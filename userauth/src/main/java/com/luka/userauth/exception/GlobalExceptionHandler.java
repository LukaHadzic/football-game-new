package com.luka.userauth.exception;

import com.luka.userauth.exception.exceptionclasses.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> jwtInvalidToken(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> userAlreadyExists(UserAlreadyExistsException userAlreadyExistsException) {
        return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<String> registrationFailed(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(registrationFailedException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenNotValidException.class)
    public ResponseEntity<String> tokenInvalid(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(registrationFailedException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<String> verificationFailed(VerificationFailedException verificationFailedException) {
        return new ResponseEntity<>(verificationFailedException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JWTInvalidException.class)
    public ResponseEntity<String> jwtInvalidToken(JWTInvalidException jWTInvalidException) {
        return new ResponseEntity<>(jWTInvalidException.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
