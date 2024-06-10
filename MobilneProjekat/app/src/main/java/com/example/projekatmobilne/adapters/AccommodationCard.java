package com.example.projekatmobilne.adapters;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.example.projekatmobilne.model.Enum.AccommodationType;

import java.io.Serializable;

public class AccommodationCard implements Serializable {
    private Bitmap imageBitmap;
    private String name;
    private String address;
    private String type;
    private String isPerPerson;
    private String oneNightPrice;
    private String totalPrice;
    private Double rating;
    private String amenities;
    private String guests;
    private String dateRange;
    private Long accommodationId;


    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public AccommodationCard(String address, String guests, String type,
                             String isPerPerson, String oneNightPrice, String totalPrice, String name,
                             String amenities, Double rating, Long accommodationId, String dateRange) {
        this.address = address;
        this.guests = guests;
        this.type = type;
        this.isPerPerson = isPerPerson;
        this.oneNightPrice = oneNightPrice;
        this.totalPrice = totalPrice;
        this.name = name;
        this.amenities = amenities;
        this.rating = rating;
        this.accommodationId = accommodationId;
        this.dateRange = dateRange;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPerPerson() {
        return isPerPerson;
    }

    public void setPerPerson(String perPerson) {
        isPerPerson = perPerson;
    }

    public String getOneNightPrice() {
        return oneNightPrice;
    }

    public void setOneNightPrice(String oneNightPrice) {
        this.oneNightPrice = oneNightPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getIsPerPerson() {
        return isPerPerson;
    }

    public void setIsPerPerson(String isPerPerson) {
        this.isPerPerson = isPerPerson;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }

    @Override
    public String toString() {
        return "AccommodationCard{" +
                "imageBitmap=" + imageBitmap +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", type=" + type +
                ", isPerPerson=" + isPerPerson +
                ", oneNightPrice=" + oneNightPrice +
                ", totalPrice=" + totalPrice +
                ", rating=" + rating +
                ", amenities='" + amenities + '\'' +
                ", guests='" + guests + '\'' +
                '}';
    }
}
