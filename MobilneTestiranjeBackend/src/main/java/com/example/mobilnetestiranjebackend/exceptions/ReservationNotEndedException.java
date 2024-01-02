package com.example.mobilnetestiranjebackend.exceptions;

public class ReservationNotEndedException extends RuntimeException{
    private String message;

    public ReservationNotEndedException () {}

    public ReservationNotEndedException(String msg)
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