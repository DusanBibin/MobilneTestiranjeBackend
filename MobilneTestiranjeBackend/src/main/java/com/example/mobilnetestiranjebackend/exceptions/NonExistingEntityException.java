package com.example.mobilnetestiranjebackend.exceptions;

public class NonExistingEntityException extends RuntimeException{

    private String message;

    public NonExistingEntityException() {}

    public NonExistingEntityException(String msg)
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
