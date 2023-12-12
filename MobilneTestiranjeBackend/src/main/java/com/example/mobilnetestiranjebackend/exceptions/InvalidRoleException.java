package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidRoleException extends RuntimeException{
    private String message;

    public InvalidRoleException() {}

    public InvalidRoleException(String msg)
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
