package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidDateException extends RuntimeException{

    private String message;

    public InvalidDateException() {}

    public InvalidDateException(String msg)
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
