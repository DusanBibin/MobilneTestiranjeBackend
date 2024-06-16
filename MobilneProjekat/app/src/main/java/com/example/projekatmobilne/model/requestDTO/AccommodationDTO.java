package com.example.projekatmobilne.model.requestDTO;


import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;
import com.example.projekatmobilne.model.Enum.RequestStatus;

import java.util.List;

public class AccommodationDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double lat;
    private Double lon;
    private List<Amenity> amenities;
    private Long minGuests;
    private Long maxGuests;
    private AccommodationType accommodationType;
    private Boolean autoAcceptEnabled;
    private List<AvailabilityDTO> availabilityList;
    private RequestStatus status;
    private String reason;
    public AccommodationDTO(){};
    public AccommodationDTO(Long id, String name, String description, String address, Double lat, Double lon,
                            List<Amenity> amenities, Long minGuests, Long maxGuests,
                            AccommodationType accommodationType, Boolean autoAcceptEnabled,
                            List<AvailabilityDTO> availabilityList, RequestStatus status, String reason) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.amenities = amenities;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.accommodationType = accommodationType;
        this.autoAcceptEnabled = autoAcceptEnabled;
        this.availabilityList = availabilityList;
        this.status = status;
        this.reason = reason;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public Long getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(Long minGuests) {
        this.minGuests = minGuests;
    }

    public Long getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Long maxGuests) {
        this.maxGuests = maxGuests;
    }

    public AccommodationType getAccommodationType() {
        return accommodationType;
    }

    public void setAccommodationType(AccommodationType accommodationType) {
        this.accommodationType = accommodationType;
    }

    public Boolean getAutoAcceptEnabled() {
        return autoAcceptEnabled;
    }

    public void setAutoAcceptEnabled(Boolean autoAcceptEnabled) {
        this.autoAcceptEnabled = autoAcceptEnabled;
    }

    public List<AvailabilityDTO> getAvailabilityList() {
        return availabilityList;
    }

    public void setAvailabilityList(List<AvailabilityDTO> availabilityList) {
        this.availabilityList = availabilityList;
    }

    @Override
    public String toString() {
        return "AccommodationDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", amenities=" + amenities +
                ", minGuests=" + minGuests +
                ", maxGuests=" + maxGuests +
                ", accommodationType=" + accommodationType +
                ", autoAcceptEnabled=" + autoAcceptEnabled +
                ", availabilityList=" + availabilityList +
                '}';
    }
}