package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.enums.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Array;
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
                .ownerReviews(new ArrayList<>())
                .build();
        ownerDusan = ownerRepository.save(ownerDusan);
        Owner ownerSomeoneElse = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("probamejl3@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .build();

        ownerSomeoneElse = ownerRepository.save(ownerSomeoneElse);

        Guest guestDusan1 = Guest.builder()
                .firstName("Dusan1")
                .lastname("Bibin1")
                .email("probamejl1@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("0691817839")
                .reservations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .favorites(new ArrayList<>())
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .build();


        Guest guestDusan2 = Guest.builder()
                .firstName("Dusan2")
                .lastname("Bibin2")
                .email("probamejl2@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .reservations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .favorites(new ArrayList<>())
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .build();

        ownerDusan = ownerRepository.save(ownerDusan);
        guestDusan1 = guestRepository.save(guestDusan1);
        guestDusan2 = guestRepository.save(guestDusan2);

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
        accommodationRequest = accommodationRequestRepository.save(accommodationRequest);

        var availabilityRequest = AvailabilityRequest.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .cancelDeadline(LocalDate.now().plusDays(4))
                .price(200L)
                .pricePerGuest(true)
                .build();

        availabilityRequest = availabilityRequestRepository.save(availabilityRequest);

        accommodationRequest = accommodationRequestRepository.save(accommodationRequest);

        ownerDusan.getAccommodationRequests().add(accommodationRequest);
        ownerDusan = ownerRepository.save(ownerDusan);

        accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        accommodationRequest = accommodationRequestRepository.save(accommodationRequest);


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
                .accommodationReviews(new ArrayList<>())
                .build();
        accommodation = accommodationRepository.save(accommodation);

        ownerDusan.getAccommodations().add(accommodation);
        ownerDusan = ownerRepository.save(ownerDusan);

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




        availabilityEditTest = availabilityRepository.save(availabilityEditTest);
        availabilityDeleteTest = availabilityRepository.save(availabilityDeleteTest);
        availabilityNew = availabilityRepository.save(availabilityNew);

        accommodation.getAvailabilityList().add(availabilityEditTest);
        accommodation.getAvailabilityList().add(availabilityDeleteTest);

        accommodation = accommodationRepository.save(accommodation);

        var reservation = Reservation.builder()
                .availability(availabilityDeleteTest)
                .guest(guestDusan1)
                .status(ReservationStatus.ACCEPTED)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now().minusDays(2))
                .reservationStartDate(LocalDate.now().minusDays(1))
                .guestNum(1L)
                .build();



        reservation = reservationRepository.save(reservation);

        guestDusan1.getReservations().add(reservation);
        guestDusan1 = guestRepository.save(guestDusan1);

        accommodation.getReservations().add(reservation);
        accommodation = accommodationRepository.save(accommodation);



    }
}