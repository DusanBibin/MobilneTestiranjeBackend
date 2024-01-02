package com.example.mobilnetestiranjebackend.exceptions;


public class EntityAlreadyExistsException extends RuntimeException{
    private String message;

    public EntityAlreadyExistsException() {}

    public EntityAlreadyExistsException(String msg)
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