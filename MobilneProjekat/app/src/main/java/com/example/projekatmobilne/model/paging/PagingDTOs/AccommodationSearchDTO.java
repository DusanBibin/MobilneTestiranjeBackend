package com.example.projekatmobilne.model.paging.PagingDTOs;

import android.graphics.Bitmap;

import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;

import java.util.List;

public class AccommodationSearchDTO {
    public AccommodationSearchDTO(Long accommodationId, String name, String address,
                                  List<Amenity> amenities, Long totalPrice, Long oneNightPrice,
                                  Boolean isPerPerson, Long minGuests, Long maxGuests,
                                  AccommodationType accommodationType, Double rating) {
        this.accommodationId = accommodationId;
        this.name = name;
        this.address = address;
        this.amenities = amenities;
        this.totalPrice = totalPrice;
        this.oneNightPrice = oneNightPrice;
        this.isPerPerson = isPerPerson;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.accommodationType = accommodationType;
        this.rating = rating;
    }

    public AccommodationSearchDTO(){}

    @Override
    public String toString() {
        return "AccommodationSearchDTO{" +
                "accommodationId=" + accommodationId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", amenities=" + amenities +
                ", totalPrice=" + totalPrice +
                ", oneNightPrice=" + oneNightPrice +
                ", isPerPerson=" + isPerPerson +
                ", minGuests=" + minGuests +
                ", maxGuests=" + maxGuests +
                ", accommodationType='" + accommodationType + '\'' +
                ", rating=" + rating +
                '}';
    }
    private Bitmap imageBitmap;
    private String name;
    private String address;
    private AccommodationType accommodationType;
    private Boolean isPerPerson;
    private Long oneNightPrice;
    private Long totalPrice;
    private Double rating;
    private List<Amenity> amenities;
    private Long minGuests;
    private Long maxGuests;



    private Long accommodationId;












    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getOneNightPrice() {
        return oneNightPrice;
    }

    public void setOneNightPrice(Long oneNightPrice) {
        this.oneNightPrice = oneNightPrice;
    }

    public Boolean getPerPerson() {
        return isPerPerson;
    }

    public void setPerPerson(Boolean perPerson) {
        isPerPerson = perPerson;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
