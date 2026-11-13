package com.example.onboarding.contact.exception;

public class DuplicateContactException extends RuntimeException{
    public   DuplicateContactException(String message){
        super(message);
    }
}
