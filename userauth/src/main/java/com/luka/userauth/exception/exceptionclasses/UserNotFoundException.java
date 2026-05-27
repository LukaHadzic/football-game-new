package com.luka.userauth.exception.exceptionclasses;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {
        super(message);
    }
}
