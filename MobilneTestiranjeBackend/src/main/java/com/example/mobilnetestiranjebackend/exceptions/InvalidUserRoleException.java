package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidUserRoleException extends RuntimeException{
    private String message;

    public InvalidUserRoleException() {}

    public InvalidUserRoleException(String msg)
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
