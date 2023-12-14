package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidEnumValueException extends RuntimeException{
    private String message;

    public InvalidEnumValueException() {}

    public InvalidEnumValueException(String msg)
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
