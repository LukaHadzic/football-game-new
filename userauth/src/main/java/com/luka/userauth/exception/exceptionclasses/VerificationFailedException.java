package com.luka.userauth.exception.exceptionclasses;

public class VerificationFailedException extends RuntimeException{

    public VerificationFailedException(String message){
        super(message);
    }
}
