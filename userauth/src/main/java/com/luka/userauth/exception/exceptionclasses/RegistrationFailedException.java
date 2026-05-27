package com.luka.userauth.exception.exceptionclasses;

public class RegistrationFailedException extends RuntimeException{

    public RegistrationFailedException(String message) {
        super(message);
    }
}
