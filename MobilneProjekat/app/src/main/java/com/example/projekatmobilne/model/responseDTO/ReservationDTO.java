package com.example.projekatmobilne.model.responseDTO;


import java.time.LocalDate;



public class ReservationDTO {

    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private Long guestNum;

    public ReservationDTO(LocalDate reservationStartDate, LocalDate reservationEndDate, Long guestNum) {
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.guestNum = guestNum;
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
}
