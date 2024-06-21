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
private final AccommodationReviewRepository accommodationReviewRepository;
private final OwnerReviewRepository ownerReviewRepository;
private final ReviewComplaintRepository reviewComplaintRepository;
private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {




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
                .email("probamejl3@gmail.com")
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

        ownerDusan = ownerRepository.save(ownerDusan);
        guestDusan1 = guestRepository.save(guestDusan1);
        guestDusan2 = guestRepository.save(guestDusan2);


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

        accommodation.getAvailabilityList().add(availabilityEditTest);
        accommodation.getAvailabilityList().add(availabilityDeleteTest);

        accommodation = accommodationRepository.save(accommodation);




         //enddate 10 startdate 8 ako je u buducnsti stavi plus days
//        var reservationOlder = Reservation.builder()
//                .availability(null)
//                .guest(guestDusan2)
//                .status(ReservationStatus.ACCEPTED)
//                .accommodation(accommodation)
//                .reservationEndDate(LocalDate.now().minusDays(20))
//                .reservationStartDate(LocalDate.now().minusDays(18))
//                .guestNum(1L)
//                .build();
//
//        var reservationOld = Reservation.builder()
//                .availability(null)
//                .guest(guestDusan1)
//                .status(ReservationStatus.ACCEPTED)
//                .accommodation(accommodation)
//                .reservationEndDate(LocalDate.now().minusDays(10))
//                .reservationStartDate(LocalDate.now().minusDays(8))
//                .guestNum(1L)
//                .build();
//
//        var reservationNew = Reservation.builder()
//                .availability(availabilityEditTest)
//                .guest(guestDusan1)
//                .status(ReservationStatus.ACCEPTED)
//                .accommodation(accommodation)
//                .reservationEndDate(LocalDate.now().plusDays(10))
//                .reservationStartDate(LocalDate.now().plusDays(8))
//                .guestNum(1L)
//                .build();
//
//
//        reservationOlder = reservationRepository.save(reservationOlder);
//        reservationNew = reservationRepository.save(reservationNew);
//        reservationOld = reservationRepository.save(reservationOld);
//
//        guestDusan1.getReservations().add(reservationOld);
//        guestDusan1.getReservations().add(reservationNew);
//        guestDusan2.getReservations().add(reservationOlder);
//        guestDusan1 = guestRepository.save(guestDusan1);
//        guestDusan2 = guestRepository.save(guestDusan2);
//
//        accommodation.getReservations().add(reservationOlder);
//        accommodation.getReservations().add(reservationOld);
//        accommodation.getReservations().add(reservationNew);
//        accommodation = accommodationRepository.save(accommodation);
//
//
//
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