package com.example.mobilnetestiranjebackend.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationViewDTO {
    private Long id;
    private String name;
    private String address;
}
