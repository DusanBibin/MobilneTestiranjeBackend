package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.enums.*;
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
private final GuestRepository guestRepository;
private final ReservationRepository reservationRepository;

    @Override
    public void run(String... args) throws Exception {
        Owner ownerDusan = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("probamejl@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .build();
        ownerRepository.save(ownerDusan);
        Owner ownerSomeoneElse = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("probamejl3@gmail.com")
                .password(passwordEncoder.encode("NekaSifra123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .build();

        ownerRepository.save(ownerSomeoneElse);

        Guest guestDusan1 = Guest.builder()
                .firstName("Dusan1")
                .lastname("Bibin1")
                .email("probamejl1@gmail.com")
                .password(passwordEncoder.encode("NekaSifra123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .build();


        Guest guestDusan2 = Guest.builder()
                .firstName("Dusan2")
                .lastname("Bibin2")
                .email("probamejl2@gmail.com")
                .password(passwordEncoder.encode("NekaSifra123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .build();

        ownerRepository.save(ownerDusan);
        guestRepository.save(guestDusan1);
        guestRepository.save(guestDusan2);

        var accommodationRequest = AccommodationRequest.builder()
                .name("AccName")
                .description("Accommodation description")
                .address("Some adresss")
                .lat(90.0)
                .lon(90.0)
                .amenities(List.of(Amenity.WIFI))
                .imagePaths(List.of("/probamejl@gmail.com/AccName/1_1704322662222_soba.png"))
                .minGuests(1L)
                .maxGuests(1L)
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
                .minGuests(1L)
                .maxGuests(1L)
                .accommodationType(AccommodationType.valueOf("STUDIO"))
                .autoAcceptEnabled(false)
                .owner(ownerDusan)
                .availabilityList(new ArrayList<>())
                .reservations(new ArrayList<>())
                .build();
        accommodationRepository.save(accommodation);
        var availabilityEditTest = Availability.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(15))
                .cancelDeadline(LocalDate.now().plusDays(4))
                .price(200L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();


        var availabilityDeleteTest = Availability.builder()
                .startDate(LocalDate.now().plusDays(15))
                .endDate(LocalDate.now().plusDays(20))
                .cancelDeadline(LocalDate.now().plusDays(8))
                .price(250L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();

        var availabilityNew = Availability.builder()
                .startDate(LocalDate.now().plusDays(20))
                .endDate(LocalDate.now().plusDays(30))
                .cancelDeadline(LocalDate.now().plusDays(12))
                .price(2500L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();



        availabilityRepository.save(availabilityEditTest);
        availabilityRepository.save(availabilityDeleteTest);
        availabilityRepository.save(availabilityNew);

        accommodation.getAvailabilityList().add(availabilityEditTest);
        accommodation.getAvailabilityList().add(availabilityDeleteTest);

        accommodationRepository.save(accommodation);
        //THIS ONE BELOW

        var reservation = Reservation.builder()
                .availability(availabilityDeleteTest)
                .guest(guestDusan1)
                .status(ReservationStatus.ACCEPTED)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now())
                .reservationStartDate(LocalDate.now())
                .guestNum(1L)
                .build();

        reservationRepository.save(reservation);
        accommodation.getReservations().add(reservation);
        accommodationRepository.save(accommodation);


    }
}