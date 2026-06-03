package com.luka.userauth.exception.exceptionclasses;

public class TokenNotValidException extends RuntimeException {

    public TokenNotValidException(String message) {
        super(message);
    }
}
