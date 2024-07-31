package com.example.projekatmobilne.model.requestDTO;

import com.example.projekatmobilne.model.Enum.ReservationStatus;

import java.time.LocalDate;


public class ReservationDTO {

    private Long availabilityId;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private Long guestNum;
    private Long price;
    private ReservationStatus status;
    private String reason;
    private String accommodationName;

    public ReservationDTO(Long availabilityId, LocalDate reservationStartDate, LocalDate reservationEndDate, Long guestNum, ReservationStatus status, String reason, String accommodationName) {
        this.availabilityId = availabilityId;
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.guestNum = guestNum;
        this.status = status;
        this.reason = reason;
        this.accommodationName = accommodationName;
    }

    public ReservationDTO(LocalDate reservationStartDate, LocalDate reservationEndDate, Long guestNum) {
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.guestNum = guestNum;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getAccommodationName() {
        return accommodationName;
    }

    public void setAccommodationName(String accommodationName) {
        this.accommodationName = accommodationName;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public LocalDate getReservationStartDate() {
        return reservationStartDate;
    }

    public void setReservationStartDate(LocalDate reservationStartDate) {
        this.reservationStartDate = reservationStartDate;
    }

    public LocalDate getReservationEndDate() {
        return reservationEndDate;
    }

    public void setReservationEndDate(LocalDate reservationEndDate) {
        this.reservationEndDate = reservationEndDate;
    }

    public Long getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Long guestNum) {
        this.guestNum = guestNum;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "availabilityId=" + availabilityId +
                ", reservationStartDate=" + reservationStartDate +
                ", reservationEndDate=" + reservationEndDate +
                ", guestNum=" + guestNum +
                ", price=" + price +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", accommodationName='" + accommodationName + '\'' +
                '}';
    }
}

