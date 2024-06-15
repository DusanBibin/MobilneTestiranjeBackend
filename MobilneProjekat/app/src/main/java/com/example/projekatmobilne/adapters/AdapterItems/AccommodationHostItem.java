package com.example.projekatmobilne.adapters.AdapterItems;

public class AccommodationHostItem {
    private Long id;
    private String name;
    private String address;
    public AccommodationHostItem(){}

    public AccommodationHostItem(String name, String address, Long id) {
        this.name = name;
        this.address = address;
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "AccommodationHostItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
