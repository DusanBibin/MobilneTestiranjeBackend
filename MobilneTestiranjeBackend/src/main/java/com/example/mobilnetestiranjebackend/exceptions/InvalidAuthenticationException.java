package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidAuthenticationException  extends RuntimeException{
    private String message;

    public InvalidAuthenticationException() {}

    public InvalidAuthenticationException(String msg)
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