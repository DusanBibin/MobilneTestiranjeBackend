package com.example.projekatmobilne.adapters;

import com.example.projekatmobilne.model.Enum.AccommodationType;

public class AccommodationCard {
    private int image;
    private String name;
    private String address;
    private String guests;
    private AccommodationType type;
    private Boolean isPerPerson;
    private Long oneNightPrice;
    private Long totalPrice;

    public AccommodationCard(int image, String address, String guests, AccommodationType type, Boolean isPerPerson, Long oneNightPrice, Long totalPrice, String name) {
        this.image = image;
        this.address = address;
        this.guests = guests;
        this.type = type;
        this.isPerPerson = isPerPerson;
        this.oneNightPrice = oneNightPrice;
        this.totalPrice = totalPrice;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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
}
