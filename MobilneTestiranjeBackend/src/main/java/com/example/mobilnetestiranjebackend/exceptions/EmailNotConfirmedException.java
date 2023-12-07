package com.example.mobilnetestiranjebackend.exceptions;

public class EmailNotConfirmedException extends RuntimeException{
    public EmailNotConfirmedException(String message) {
        super(message);
    }
}
