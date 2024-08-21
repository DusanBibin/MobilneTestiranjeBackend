package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.enums.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

private final OwnerRepository ownerRepository;
private final PasswordEncoder passwordEncoder;
private final AccommodationRepository accommodationRepository;
private final AvailabilityRepository availabilityRepository;
private final GuestRepository guestRepository;
private final ReservationRepository reservationRepository;
private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        Owner ownerDusan = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("dusanbibin2@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();
        ownerDusan = ownerRepository.save(ownerDusan);
        Owner ownerSomeoneElse = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email(" ")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("654653")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();

        ownerSomeoneElse = ownerRepository.save(ownerSomeoneElse);

        Guest guestDusan1 = Guest.builder()
                .firstName("Dusan1")
                .lastname("Bibin1")
                .email("probamejl1@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("5467654324")
                .reservations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .favorites(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();


        Guest guestDusan2 = Guest.builder()
                .firstName("Dusan2")
                .lastname("Bibin2")
                .email("probamejl2@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("75436534654")
                .address("Neka ulica 123")
                .reservations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .favorites(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();



        Guest guestDusan3 = Guest.builder()
                .firstName("Dusan3")
                .lastname("Bibin3")
                .email("probamejl3@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("75436534654")
                .address("Neka ulica 123")
                .reservations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .favorites(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.GUEST)
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();

        ownerDusan = ownerRepository.save(ownerDusan);
        guestDusan1 = guestRepository.save(guestDusan1);
        guestDusan2 = guestRepository.save(guestDusan2);
        guestDusan3 = guestRepository.save(guestDusan3);


        Admin admin = Admin.builder()
                .firstName("admin")
                .lastname("admin")
                .email("admin")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("43264235")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.ADMIN)
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();

        admin = userRepository.save(admin);


        var accommodation = Accommodation.builder()
                .name("NewAcc")
                .description("Accommodation description")
                .address("Some address")
                .lat(45.25)
                .lon(19.85)
                .amenities(List.of(Amenity.WIFI, Amenity.AC))
                .imagePaths(List.of("/dusanbibin2@gmail.com/NewAcc/1_new_room.jpg",
                        "/dusanbibin2@gmail.com/NewAcc/2_new_room.jpg",
                        "/dusanbibin2@gmail.com/NewAcc/3_new_room.jpg",
                        "/dusanbibin2@gmail.com/NewAcc/4_new_room.jpg",
                        "/dusanbibin2@gmail.com/NewAcc/5_new_room.jpg"))
                .minGuests(1L)
                .maxGuests(4L)
                .accommodationType(AccommodationType.valueOf("STUDIO"))
                .autoAcceptEnabled(true )
                .owner(ownerDusan)
                .availabilityList(new ArrayList<>())
                .reservations(new ArrayList<>())
                .accommodationReviews(new ArrayList<>())
                .build();
        accommodation = accommodationRepository.save(accommodation);

        ownerDusan.getAccommodations().add(accommodation);
        ownerDusan = ownerRepository.save(ownerDusan);

        var availabilityOld = Availability.builder()
                .startDate(LocalDate.now().minusDays(20))
                .endDate(LocalDate.now().minusDays(5))
                .cancelDeadline(LocalDate.now().minusDays(25))
                .price(50L)
                .pricePerGuest(false)
                .accommodation(accommodation)
                .build();

        var availabilityEditTest = Availability.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(15))
                .cancelDeadline(LocalDate.now().plusDays(4))
                .price(200L)
                .pricePerGuest(false)
                .accommodation(accommodation)
                .build();


        var availabilityDeleteTest = Availability.builder()
                .startDate(LocalDate.now().plusDays(16))
                .endDate(LocalDate.now().plusDays(20))
                .cancelDeadline(LocalDate.now().plusDays(8))
                .price(250L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();

        var availabilityNew = Availability.builder()
                .startDate(LocalDate.now().plusDays(21))
                .endDate(LocalDate.now().plusDays(30))
                .cancelDeadline(LocalDate.now().plusDays(12))
                .price(2500L)
                .pricePerGuest(true)
                .accommodation(accommodation)
                .build();

        availabilityEditTest = availabilityRepository.save(availabilityEditTest);
        availabilityDeleteTest = availabilityRepository.save(availabilityDeleteTest);
        availabilityNew = availabilityRepository.save(availabilityNew);
        availabilityOld = availabilityRepository.save(availabilityOld);

        accommodation.getAvailabilityList().add(availabilityEditTest);
        accommodation.getAvailabilityList().add(availabilityDeleteTest);

        accommodation = accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .availability(availabilityEditTest)
                .guest(guestDusan1)
                .status(ReservationStatus.PENDING)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now().plusDays(11))
                .reservationStartDate(LocalDate.now().plusDays(7))
                .cancelDeadline(availabilityEditTest.getCancelDeadline())
                .price((ChronoUnit.DAYS.between(LocalDate.now().minusDays(11), LocalDate.now().minusDays(7)) + 1) * availabilityEditTest.getPrice())
                .unitPrice(availabilityEditTest.getPrice())
                .guestNum(1L)
                .perGuest(false)
                .build();

        var reservation2 = Reservation.builder()
                .availability(availabilityEditTest)
                .guest(guestDusan2)
                .status(ReservationStatus.PENDING)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now().plusDays(11))
                .reservationStartDate(LocalDate.now().plusDays(7))
                .cancelDeadline(availabilityEditTest.getCancelDeadline())
                .price((ChronoUnit.DAYS.between(LocalDate.now().minusDays(11), LocalDate.now().minusDays(7)) + 1) * availabilityEditTest.getPrice())
                .unitPrice(availabilityEditTest.getPrice())
                .perGuest(false)
                .guestNum(1L)
                .build();

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
         //enddate 10 startdate 8 ako je u buducnsti stavi plus days

        var reservationOld1 = Reservation.builder()
                .availability(availabilityOld)
                .guest(guestDusan2)
                .status(ReservationStatus.ACCEPTED)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now().minusDays(17))
                .reservationStartDate(LocalDate.now().minusDays(18))
                .cancelDeadline(availabilityOld.getCancelDeadline())
                .price((ChronoUnit.DAYS.between(LocalDate.now().minusDays(11), LocalDate.now().minusDays(7)) + 1) * availabilityOld.getPrice())
                .unitPrice(availabilityOld.getPrice())
                .perGuest(false)
                .guestNum(1L)
                .build();

        var reservationOld2 = Reservation.builder()
                .availability(availabilityOld)
                .guest(guestDusan1)
                .status(ReservationStatus.ACCEPTED)
                .accommodation(accommodation)
                .reservationEndDate(LocalDate.now().minusDays(6))
                .reservationStartDate(LocalDate.now().minusDays(3))
                .cancelDeadline(availabilityOld.getCancelDeadline())
                .price((ChronoUnit.DAYS.between(LocalDate.now().minusDays(11), LocalDate.now().minusDays(7)) + 1) * availabilityOld.getPrice())
                .unitPrice(availabilityOld.getPrice())
                .perGuest(false)
                .guestNum(1L)
                .build();



        reservationOld1 = reservationRepository.save(reservationOld1);
        reservationOld2 = reservationRepository.save(reservationOld2);

        guestDusan1.getReservations().add(reservationOld2);
        guestDusan2.getReservations().add(reservationOld1);
        guestDusan1 = guestRepository.save(guestDusan1);
        guestDusan2 = guestRepository.save(guestDusan2);


//        var ownerReview = OwnerReview.builder()
//                .rating(5L)
//                .comment("owner Review")
//                .owner(ownerDusan)
//                .guest(guestDusan1)
//                .complaint(null)
//                .build();
//
//        ownerReview = ownerReviewRepository.save(ownerReview);
//
//        var accommodationReview = AccommodationReview.builder()
//                .rating(3L)
//                .comment("accommodation review")
//                .allowed(false)
//                .guest(guestDusan1)
//                .reservation(reservationOld)
//                .accommodation(accommodation)
//                .complaint(null)
//                .build();
//
//        accommodationReview = accommodationReviewRepository.save(accommodationReview);
//
//
//
//
//
//        var ownerReviewOlder = OwnerReview.builder()
//                .rating(5L)
//                .comment("owner Review")
//                .owner(ownerDusan)
//                .guest(guestDusan2)
//                .complaint(null)
//                .build();
//
//        ownerReviewOlder = ownerReviewRepository.save(ownerReviewOlder);
//
//        var accommodationReviewOlder = AccommodationReview.builder()
//                .rating(3L)
//                .comment("accommodation review")
//                .allowed(false)
//                .guest(guestDusan2)
//                .reservation(reservationOld)
//                .accommodation(accommodation)
//                .complaint(null)
//                .build();
//
//        accommodationReviewOlder = accommodationReviewRepository.save(accommodationReviewOlder);
//
//        var ownerReviewComplaint = ReviewComplaint.builder()
//                .owner(ownerDusan)
//                .review(ownerReview)
//                .guest(guestDusan1)
//                .reason("some reason")
//                .status(RequestStatus.PENDING)
//                .response("")
//                .build();
//
//        ownerReviewComplaint = reviewComplaintRepository.save(ownerReviewComplaint);
//
//
//        var accommodationReviewComplaint = ReviewComplaint.builder()
//                .owner(ownerDusan)
//                .review(accommodationReview)
//                .guest(guestDusan1)
//                .reason("some reason")
//                .status(RequestStatus.PENDING)
//                .response("")
//                .build();
//
//
//        accommodationReviewComplaint = reviewComplaintRepository.save(accommodationReviewComplaint);
//
//
//        ownerReview.setComplaint(ownerReviewComplaint);
//        ownerReview = ownerReviewRepository.save(ownerReview);
//
//        accommodationReview.setComplaint(accommodationReviewComplaint);
//        accommodationReview = accommodationReviewRepository.save(accommodationReview);


        //thirtyMore();

        //thirtyMoreReviews(ownerDusan, guestDusan1, reservationOld, accommodation);
    }



    private void thirtyMore(){

        Owner ownerSomeoneElse = Owner.builder()
                .firstName("Neko")
                .lastname("Neko")
                .email("nekonekad@gmail.com")
                .password(passwordEncoder.encode("123"))
                .phoneNumber("654653")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .ownerReviews(new ArrayList<>())
                .reviewComplaints(new ArrayList<>())
                .emailChangeVerification(null)
                .verification(new Verification())
                .build();

        ownerSomeoneElse = ownerRepository.save(ownerSomeoneElse);


        for(int i = 0; i < 30; i++){


            var accommodation = Accommodation.builder()
                    .name("AewAcc")
                    .description("Accommodation description")
                    .address("Some address")
                    .lat(90.0)
                    .lon(90.0)
                    .amenities(List.of(Amenity.WIFI, Amenity.AC))
                    .imagePaths(List.of("/dusanbibin2@gmail.com/NewAcc/1_new_room.jpg",
                            "/dusanbibin2@gmail.com/NewAcc/2_new_room.jpg", "/dusanbibin2@gmail.com/NewAcc/3_new_room.jpg",
                            "/dusanbibin2@gmail.com/NewAcc/4_new_room.jpg", "/dusanbibin2@gmail.com/NewAcc/5_new_room.jpg"))
                    .minGuests(1L)
                    .maxGuests(10L)
                    .accommodationType(AccommodationType.valueOf("STUDIO"))
                    .autoAcceptEnabled(false)
                    .owner(ownerSomeoneElse)
                    .availabilityList(new ArrayList<>())
                    .reservations(new ArrayList<>())
                    .accommodationReviews(new ArrayList<>())
                    .build();
            accommodation = accommodationRepository.save(accommodation);

            ownerSomeoneElse.getAccommodations().add(accommodation);
            ownerSomeoneElse = ownerRepository.save(ownerSomeoneElse);

            var availabilityEditTest = Availability.builder()
                    .startDate(LocalDate.now().plusDays(5))
                    .endDate(LocalDate.now().plusDays(15))
                    .cancelDeadline(LocalDate.now().plusDays(4))
                    .price(400L)
                    .pricePerGuest(true)
                    .accommodation(accommodation)
                    .build();


            var availabilityDeleteTest = Availability.builder()
                    .startDate(LocalDate.now().plusDays(16))
                    .endDate(LocalDate.now().plusDays(20))
                    .cancelDeadline(LocalDate.now().plusDays(8))
                    .price(450L)
                    .pricePerGuest(true)
                    .accommodation(accommodation)
                    .build();

            var availabilityNew = Availability.builder()
                    .startDate(LocalDate.now().plusDays(21))
                    .endDate(LocalDate.now().plusDays(30))
                    .cancelDeadline(LocalDate.now().plusDays(12))
                    .price(4500L)
                    .pricePerGuest(true)
                    .accommodation(accommodation)
                    .build();

            availabilityEditTest = availabilityRepository.save(availabilityEditTest);
            availabilityDeleteTest = availabilityRepository.save(availabilityDeleteTest);
            availabilityNew = availabilityRepository.save(availabilityNew);

            accommodation.getAvailabilityList().add(availabilityEditTest);
            accommodation.getAvailabilityList().add(availabilityDeleteTest);

            accommodation = accommodationRepository.save(accommodation);
        }
    }
}