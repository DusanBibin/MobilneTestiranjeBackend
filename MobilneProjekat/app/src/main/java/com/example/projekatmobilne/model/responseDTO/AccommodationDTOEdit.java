package com.example.projekatmobilne.model.responseDTO;
import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;

import java.util.List;


public class AccommodationDTOEdit {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
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

    public AccommodationDTOEdit(String name, String description, String address, Double lat,
                                Double lon, Long minGuests, Long maxGuests,
                                AccommodationType accommodationType, List<Amenity> amenities,
                                Boolean autoAcceptEnabled) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.accommodationType = accommodationType;
        this.amenities = amenities;
        this.autoAcceptEnabled = autoAcceptEnabled;
    }
}
