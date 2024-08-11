package com.example.projekatmobilne.model.requestDTO;

import com.example.projekatmobilne.model.Enum.ReservationStatus;

import java.time.LocalDate;


public class ReservationDTO {

    private String accommodationName;
    private String accommodationAddress;
    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private LocalDate cancelDeadline;
    private Long guestNum;
    private Long unitPrice;
    private Boolean perGuest;
    private Long price;
    private ReservationStatus status;
    private String reason;

    private Long ownerId;
    private String ownerNameAndSurname;
    private String ownerEmail;

    private Long guestId;
    private String nameAndSurname;
    private String userEmail;
    private Long timesUserCancel;
    private Boolean conflictReservations;
    private Long accommodationId;
    private Long availabilityId;
    private Long reservationId;
    private Boolean reviewPresent;

    public Boolean getConflictReservations() {
        return conflictReservations;
    }

    public void setConflictReservations(Boolean conflictReservations) {
        this.conflictReservations = conflictReservations;
    }

    public ReservationDTO(Long reservationId, Long availabilityId, LocalDate reservationStartDate,
                          LocalDate reservationEndDate, Long guestNum, ReservationStatus status,
                          String reason, String accommodationName, String accommodationAddress,
                          Long unitPrice, Boolean perGuest,Long ownerId, String ownerNameAndSurname,
                          String ownerEmail, Long guestId, String nameAndSurname, String userEmail,
                          Long timesUserCancel, Long accommodationId, Boolean conflictReservations,
                          LocalDate cancelDeadline, Boolean reviewPresent) {
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

        this.ownerId = ownerId;
        this.ownerNameAndSurname = ownerNameAndSurname;
        this.ownerEmail = ownerEmail;

        this.guestId = guestId;
        this.nameAndSurname = nameAndSurname;
        this.userEmail = userEmail;
        this.timesUserCancel = timesUserCancel;
        this.accommodationId = accommodationId;
        this.conflictReservations = conflictReservations;
        this.cancelDeadline = cancelDeadline;
        this.reviewPresent = reviewPresent;
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

    public LocalDate getCancelDeadline() {
        return cancelDeadline;
    }

    public void setCancelDeadline(LocalDate cancelDeadline) {
        this.cancelDeadline = cancelDeadline;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerNameAndSurname() {
        return ownerNameAndSurname;
    }

    public void setOwnerNameAndSurname(String ownerNameAndSurname) {
        this.ownerNameAndSurname = ownerNameAndSurname;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public Boolean getReviewPresent() {
        return reviewPresent;
    }

    public void setReviewPresent(Boolean reviewPresent) {
        this.reviewPresent = reviewPresent;
    }

    @Override
    public String toString() {
        return "ReservationDTO{" +
                "accommodationName='" + accommodationName + '\'' +
                ", accommodationAddress='" + accommodationAddress + '\'' +
                ", reservationStartDate=" + reservationStartDate +
                ", reservationEndDate=" + reservationEndDate +
                ", cancelDeadline=" + cancelDeadline +
                ", guestNum=" + guestNum +
                ", unitPrice=" + unitPrice +
                ", perGuest=" + perGuest +
                ", price=" + price +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", ownerId=" + ownerId +
                ", ownerNameAndSurname='" + ownerNameAndSurname + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", guestId=" + guestId +
                ", nameAndSurname='" + nameAndSurname + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", timesUserCancel=" + timesUserCancel +
                ", conflictReservations=" + conflictReservations +
                ", accommodationId=" + accommodationId +
                ", availabilityId=" + availabilityId +
                ", reservationId=" + reservationId +
                ", reviewPresent=" + reviewPresent +
                '}';
    }
}

