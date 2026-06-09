package com.luka.userauth.exception.exceptionclasses;

public class JWTInvalidException extends RuntimeException{

    public JWTInvalidException(String message){
        super(message);
    }
}
