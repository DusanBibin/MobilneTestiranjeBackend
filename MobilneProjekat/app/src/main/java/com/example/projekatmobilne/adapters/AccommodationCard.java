package com.example.projekatmobilne.adapters;

import android.graphics.Bitmap;

import com.example.projekatmobilne.model.Enum.AccommodationType;

public class AccommodationCard {
    private Bitmap imageBitmap;
    private String name;
    private String address;
    private AccommodationType type;
    private Boolean isPerPerson;
    private Long oneNightPrice;
    private Long totalPrice;
    private Double rating;
    private String amenities;
    private String guests;


    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public AccommodationCard(String address, String guests, AccommodationType type,
                             Boolean isPerPerson, Long oneNightPrice, Long totalPrice, String name,
                             String amenities, Double rating) {
        this.address = address;
        this.guests = guests;
        this.type = type;
        this.isPerPerson = isPerPerson;
        this.oneNightPrice = oneNightPrice;
        this.totalPrice = totalPrice;
        this.name = name;
        this.amenities = amenities;
        this.rating = rating;
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

    public AccommodationType getType() {
        return type;
    }

    public void setType(AccommodationType type) {
        this.type = type;
    }

    public Boolean getPerPerson() {
        return isPerPerson;
    }

    public void setPerPerson(Boolean perPerson) {
        isPerPerson = perPerson;
    }

    public Long getOneNightPrice() {
        return oneNightPrice;
    }

    public void setOneNightPrice(Long oneNightPrice) {
        this.oneNightPrice = oneNightPrice;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
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
