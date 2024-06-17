package com.example.projekatmobilne.model.requestDTO;

import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.RequestType;

import java.time.LocalDate;

public class AvailabilityDTO {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate cancellationDeadline;

    private Boolean pricePerGuest;

    private Long price;

    private RequestType requestType;

    public AvailabilityDTO(){

    }
    public AvailabilityDTO(LocalDate startDate, LocalDate endDate, LocalDate cancellationDeadline, Boolean pricePerGuest, Long price) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancellationDeadline = cancellationDeadline;
        this.pricePerGuest = pricePerGuest;
        this.price = price;
    }

    public AvailabilityDTO(Long id, LocalDate startDate, LocalDate endDate, LocalDate cancellationDeadline, Boolean pricePerGuest, Long price, RequestType requestType) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancellationDeadline = cancellationDeadline;
        this.pricePerGuest = pricePerGuest;
        this.price = price;
        this.requestType = requestType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(LocalDate cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public Boolean getPricePerGuest() {
        return pricePerGuest;
    }

    public void setPricePerGuest(Boolean pricePerGuest) {
        this.pricePerGuest = pricePerGuest;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

}
