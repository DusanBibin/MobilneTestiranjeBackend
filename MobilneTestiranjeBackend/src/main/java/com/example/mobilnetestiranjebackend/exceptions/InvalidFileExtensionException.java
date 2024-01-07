package com.example.mobilnetestiranjebackend.exceptions;

public class InvalidFileExtensionException extends RuntimeException{
    private String message;

    public InvalidFileExtensionException () {}

    public InvalidFileExtensionException(String msg)
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
