package com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes;


import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.RequestType;

public class AccommodationRequestPreviewDTO {
    private Long requestId;
    private String accommodationName;
    private String accommodationAddress;
    private RequestStatus status;
    private String reason;
    private RequestType requestType;
    private String existingAccommodationName;
    private String existingAddress;

    public AccommodationRequestPreviewDTO(){}

    public AccommodationRequestPreviewDTO(String accommodationName, String accommodationAddress,
                                          RequestStatus status, String reason, RequestType requestType,
                                          String existingAddress, String existingAccommodationName,
                                          Long requestId) {
        this.accommodationName = accommodationName;
        this.accommodationAddress = accommodationAddress;
        this.status = status;
        this.reason = reason;
        this.requestType = requestType;
        this.existingAddress = existingAddress;
        this.existingAccommodationName = existingAccommodationName;
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getExistingAccommodationName() {
        return existingAccommodationName;
    }

    public void setExistingAccommodationName(String existingAccommodationName) {
        this.existingAccommodationName = existingAccommodationName;
    }

    public String getExistingAddress() {
        return existingAddress;
    }

    public void setExistingAddress(String existingAddress) {
        this.existingAddress = existingAddress;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getAccommodationName() {
        return accommodationName;
    }

    public void setAccommodationName(String accommodationName) {
        this.accommodationName = accommodationName;
    }

    public String getAccommodationAddress() {
        return accommodationAddress;
    }

    public void setAccommodationAddress(String accommodationAddress) {
        this.accommodationAddress = accommodationAddress;
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

    @Override
    public String toString() {
        return "AccommodationRequestPreviewDTO{" +
                "accommodationName='" + accommodationName + '\'' +
                ", accommodationAddress='" + accommodationAddress + '\'' +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
}

