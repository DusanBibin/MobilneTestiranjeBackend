package com.example.projekatmobilne.model.requestDTO;

import com.example.projekatmobilne.model.Enum.ReservationStatus;

import java.time.LocalDate;


public class ReservationDTO {


    private String accommodationName;
    private String accommodationAddress;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private Long guestNum;
    private Long unitPrice;
    private Boolean perGuest;
    private Long price;
    private ReservationStatus status;
    private String reason;
    private String nameAndSurname;
    private String userEmail;
    private Long timesUserCancel;
    private Boolean conflictReservations;
    private Long accommodationId;
    private Long availabilityId;
    private Long reservationId;

    public Boolean getConflictReservations() {
        return conflictReservations;
    }

    public void setConflictReservations(Boolean conflictReservations) {
        this.conflictReservations = conflictReservations;
    }

    public ReservationDTO(Long reservationId, Long availabilityId, LocalDate reservationStartDate,
                          LocalDate reservationEndDate, Long guestNum, ReservationStatus status,
                          String reason, String accommodationName, String accommodationAddress,
                          Long unitPrice, Boolean perGuest, String nameAndSurname, String userEmail,
                          Long timesUserCancel, Long accommodationId, Boolean conflictReservations) {
        this.availabilityId = availabilityId;
        this.reservationId = reservationId;
        this.reservationStartDate = reservationStartDate;
        this.reservationEndDate = reservationEndDate;
        this.guestNum = guestNum;
        this.status = status;
        this.reason = reason;
        this.accommodationName = accommodationName;
        this.accommodationAddress = accommodationAddress;
        this.unitPrice = unitPrice;
        this.perGuest = perGuest;
        this.nameAndSurname = nameAndSurname;
        this.userEmail = userEmail;
        this.timesUserCancel = timesUserCancel;
        this.accommodationId = accommodationId;
        this.conflictReservations = conflictReservations;
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

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getAccommodationAddress() {
        return accommodationAddress;
    }

    public void setAccommodationAddress(String accommodationAddress) {
        this.accommodationAddress = accommodationAddress;
    }

    public Boolean getPerGuest() {
        return perGuest;
    }

    public void setPerGuest(Boolean perGuest) {
        this.perGuest = perGuest;
    }

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public void setNameAndSurname(String nameAndSurname) {
        this.nameAndSurname = nameAndSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getTimesUserCancel() {
        return timesUserCancel;
    }

    public void setTimesUserCancel(Long timesUserCancel) {
        this.timesUserCancel = timesUserCancel;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "accommodationName='" + accommodationName + '\'' +
                ", accommodationAddress='" + accommodationAddress + '\'' +
                ", reservationStartDate=" + reservationStartDate +
                ", reservationEndDate=" + reservationEndDate +
                ", guestNum=" + guestNum +
                ", unitPrice=" + unitPrice +
                ", perGuest=" + perGuest +
                ", price=" + price +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", nameAndSurname='" + nameAndSurname + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", timesUserCancel=" + timesUserCancel +
                ", conflictReservations=" + conflictReservations +
                ", accommodationId=" + accommodationId +
                ", availabilityId=" + availabilityId +
                ", reservationId=" + reservationId +
                '}';
    }
}

