package com.example.mobilnetestiranjebackend.exceptions;


public class AccommodationAlreadyExistsException extends RuntimeException{
    private String message;

    public AccommodationAlreadyExistsException() {}

    public AccommodationAlreadyExistsException(String msg)
    {
        super(msg);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}