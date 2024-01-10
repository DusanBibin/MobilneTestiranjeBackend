package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

private final OwnerRepository ownerRepository;
private final PasswordEncoder passwordEncoder;
private final AccommodationRepository accommodationRepository;
private final AvailabilityRepository availabilityRepository;
private final AccommodationRequestRepository accommodationRequestRepository;
private final AvailabilityRequestRepository availabilityRequestRepository;

    @Override
    public void run(String... args) throws Exception {
        Owner ownerDusan = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("probamejl@gmail.com")
                .password(passwordEncoder.encode("NekaSifra123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .build();

        ownerRepository.save(ownerDusan);

        var accommodationRequest = AccommodationRequest.builder()
                .name("AccName")
                .description("Accommodation description")
                .address("Some adresss")
                .lat(90.0)
                .lon(90.0)
                .amenities(List.of(Amenity.WIFI))
                .imagePaths(List.of("/probamejl@gmail.com/AccName/1_1704322662222_soba.png"))
                .minGuests(1)
                .maxGuests(1)
                .accommodationType(AccommodationType.valueOf("STUDIO"))
                .autoAcceptEnabled(false)
                .availabilityRequests(new ArrayList<>())
                .owner(ownerDusan)
                .status(RequestStatus.PENDING)
                .build();

        accommodationRequestRepository.save(accommodationRequest);

        var availabilityRequest = AvailabilityRequest.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .cancelDeadline(LocalDate.now().plusDays(4))
                .price(200L)
                .pricePerGuest(true)
                .accommodationRequest(accommodationRequest)
                .build();

        availabilityRequestRepository.save(availabilityRequest);
        accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        accommodationRequestRepository.save(accommodationRequest);

        var accommodation = Accommodation.builder()
                .name("NewAcc")
                .description("Accommodation description")
                .address("Some address")
                .lat(90.0)
                .lon(90.0)
                .amenities(List.of(Amenity.WIFI))
                .imagePaths(List.of("/probamejl@gmail.com/NewAcc/1_new_room.jpg",
                        "/probamejl@gmail.com/NewAcc/2_new_room.jpg"))
                .minGuests(1)
                .maxGuests(1)
                .accommodationType(AccommodationType.valueOf("STUDIO"))
                .autoAcceptEnabled(false)
                .owner(ownerDusan)
                .availabilityList(new ArrayList<>())
                .reservations(new ArrayList<>())
                .build();

        accommodationRepository.save(accommodation);

        var availabilityEditTest = AccommodationAvailability.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .cancelDeadline(LocalDate.now().plusDays(4))
                .price(200L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();


        var availabilityDeleteTest = AccommodationAvailability.builder()
                .startDate(LocalDate.now().plusDays(11))
                .endDate(LocalDate.now().plusDays(15))
                .cancelDeadline(LocalDate.now().plusDays(8))
                .price(250L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();


        availabilityRepository.save(availabilityEditTest);
        availabilityRepository.save(availabilityDeleteTest);

        accommodation.getAvailabilityList().add(availabilityEditTest);
        accommodation.getAvailabilityList().add(availabilityDeleteTest);

        accommodationRepository.save(accommodation);






    }
}