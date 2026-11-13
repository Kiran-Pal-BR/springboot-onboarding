package com.example.onboarding.domain.exception;

public class InvalidAmountException extends IllegalArgumentException{
    public InvalidAmountException(String message){
        super(message);
    }
}
