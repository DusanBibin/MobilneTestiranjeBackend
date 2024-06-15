package com.example.projekatmobilne.model.responseDTO.innerDTO;

import java.time.LocalDate;

public class AvailabilityDTOInner {
    private Long id;
    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate cancellationDeadline;

    private Boolean pricePerGuest;

    private Long price;

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

    public AvailabilityDTOInner(Long id, LocalDate startDate, LocalDate endDate, LocalDate cancellationDeadline, Boolean pricePerGuest, Long price) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancellationDeadline = cancellationDeadline;
        this.pricePerGuest = pricePerGuest;
        this.price = price;
    }

    @Override
    public String toString() {
        return "AvailabilityDTOResponse{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", cancellationDeadline=" + cancellationDeadline +
                ", pricePerGuest=" + pricePerGuest +
                ", price=" + price +
                '}';
    }
}