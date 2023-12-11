package com.example.mobilnetestiranjebackend.exceptions;

public class UserAlreadyConfirmedException extends RuntimeException{
    private String message;

    public UserAlreadyConfirmedException() {}

    public UserAlreadyConfirmedException(String msg)
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
