package com.example.projekatmobilne.model.responseDTO;

import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.requestDTO.AvailabilityDTO;
import com.example.projekatmobilne.model.responseDTO.innerDTO.AvailabilityDTOInner;

import java.util.List;

public class AccommodationDifferencesDTO {


    private AccommodationDTOEdit accommodationInfo;
    private AccommodationDTOEdit requestAccommodationInfo;

    private List<AvailabilityDTO> availabilities;
    private List<AvailabilityDTO> requestAvailabilities;

    private List<String> imagesToAdd;
    private List<String> imagesToRemove;
    private List<String> currentImages;

    private RequestStatus status;
    private String reason;

    public AccommodationDTOEdit getAccommodationInfo() {
        return accommodationInfo;
    }

    public void setAccommodationInfo(AccommodationDTOEdit accommodationInfo) {
        this.accommodationInfo = accommodationInfo;
    }

    public List<AvailabilityDTO> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailabilityDTO> availabilities) {
        this.availabilities = availabilities;
    }

    public AccommodationDTOEdit getRequestAccommodationInfo() {
        return requestAccommodationInfo;
    }

    public void setRequestAccommodationInfo(AccommodationDTOEdit requestAccommodationInfo) {
        this.requestAccommodationInfo = requestAccommodationInfo;
    }

    public List<AvailabilityDTO> getRequestAvailabilities() {
        return requestAvailabilities;
    }

    public void setRequestAvailabilities(List<AvailabilityDTO> requestAvailabilities) {
        this.requestAvailabilities = requestAvailabilities;
    }

    public List<String> getImagesToAdd() {
        return imagesToAdd;
    }

    public void setImagesToAdd(List<String> imagesToAdd) {
        this.imagesToAdd = imagesToAdd;
    }

    public List<String> getImagesToRemove() {
        return imagesToRemove;
    }

    public void setImagesToRemove(List<String> imagesToRemove) {
        this.imagesToRemove = imagesToRemove;
    }

    public List<String> getCurrentImages() {
        return currentImages;
    }

    public void setCurrentImages(List<String> currentImages) {
        this.currentImages = currentImages;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AccommodationDifferencesDTO(AccommodationDTOEdit accommodationInfo,
                                       AccommodationDTOEdit requestAccommodationInfo,
                                       List<AvailabilityDTO> availabilities,
                                       List<AvailabilityDTO> requestAvailabilities,
                                       List<String> imagesToAdd, RequestStatus status,
                                       String reason, List<String> imagesToRemove,
                                       List<String> currentImages) {
        this.accommodationInfo = accommodationInfo;
        this.requestAccommodationInfo = requestAccommodationInfo;
        this.availabilities = availabilities;
        this.requestAvailabilities = requestAvailabilities;
        this.imagesToAdd = imagesToAdd;
        this.status = status;
        this.reason = reason;
        this.imagesToRemove = imagesToRemove;
        this.currentImages = currentImages;
    }

    @Override
    public String toString() {
        return "AccommodationDifferencesDTO{" +
                "accommodationInfo=" + accommodationInfo +
                ", requestAccommodationInfo=" + requestAccommodationInfo +
                ", availabilities=" + availabilities +
                ", requestAvailabilities=" + requestAvailabilities +
                ", imagesToAdd=" + imagesToAdd +
                ", imagesToRemove=" + imagesToRemove +
                ", currentImages=" + currentImages +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}
