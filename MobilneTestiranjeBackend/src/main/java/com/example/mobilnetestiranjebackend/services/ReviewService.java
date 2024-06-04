package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReviewDTOResponse;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OwnerRepository ownerRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationRepository accommodationRepository;

    public void createOwnerReview(ReviewDTO reviewDTO, Long ownerId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        if(ownerWrapper.isEmpty()) throw new NonExistingEntityException("Owner with this id doesn't exist");
        var owner = ownerWrapper.get();

        var reviews = ownerReviewRepository.findOwnerReviewsByGuestId(guestId, ownerId);
        if(!reviews.isEmpty()) throw new InvalidAuthorizationException("You already have a review for this owner");

        var completedReservations = reservationRepository.findGuestCompletedReservations(ownerId, guestId);
        if(completedReservations.isEmpty())
            throw new InvalidAuthorizationException("You cannot leave a comment if you didn't have areservation");


        OwnerReview ownerReview = OwnerReview.builder()
                .owner(owner)
                .guest(guest)
                .comment(reviewDTO.getComment())
                .rating(reviewDTO.getRating())
                .build();


        ownerReview = ownerReviewRepository.save(ownerReview);

        guest.getOwnerReviews().add(ownerReview);
        guestRepository.save(guest);

        owner.getOwnerReviews().add(ownerReview);
        ownerRepository.save(owner);

    }

    public void deleteOwnerReview(Long reviewId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");


        var reviewWrapper = ownerReviewRepository.findByReviewIdAndGuestId(reviewId, guestId);
        if(reviewWrapper.isEmpty()) throw new NonExistingEntityException("Owner review with this id doesn't exist");
        var review = reviewWrapper.get();

        ownerReviewRepository.delete(review);

    }

    public void createAccommodationReview(ReviewDTO reviewDTO, Long accommodationId, Long reservationId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        var accommodation = accommodationWrapper.get();

        var reservationWrapper = reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");

        reservationWrapper = reservationRepository.findByIdAndGuest(reservationId, guestId);
        if(reservationWrapper.isEmpty()) throw new InvalidAuthorizationException("You don't own this reservation");
        var reservation = reservationWrapper.get();

        var reviewWrapper = accommodationReviewRepository.findByReservationIdAndGuestId(reservationId, guestId);
        if(!reviewWrapper.isEmpty()) throw new InvalidAuthorizationException("You already made a review for this reservation");


        var endDate = reservation.getReservationEndDate();
        if(endDate.isBefore(LocalDate.now().minusDays(7))) throw new InvalidDateException("7 days have passed, you cannot create a review anymore");


        var accommodationReview = AccommodationReview.builder()
                .allowed(false)
                .comment(reviewDTO.getComment())
                .rating(reviewDTO.getRating())
                .guest(guest)
                .reservation(reservation)
                .accommodation(accommodation)
                .build();

        accommodationReview = accommodationReviewRepository.save(accommodationReview);

        accommodation.getAccommodationReviews().add(accommodationReview);
        accommodationRepository.save(accommodation);

    }

    public void deleteAccommodationReview(Long reviewId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");

        var reviewWrapper = accommodationReviewRepository.findByReviewIdAndGuestId(reviewId, guestId);
        if(reviewWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation review with this id doesn't exist");
        var review = reviewWrapper.get();

        accommodationReviewRepository.delete(review);
    }

    public Page<ReviewDTOResponse> getAccommodationReviews(Long accommodationId, int pageNo, int pageSize) {
        var accommodationWrapper = accommodationRepository.findById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");

        var reservations = reservationRepository.findReservationsEndedByAccommodationId(accommodationId);

        List<ReviewDTOResponse> reviews = new ArrayList<>();
        for(Reservation r: reservations){
            var guestId = r.getGuest().getId();

            var ownerReview = ownerReviewRepository.findByAccommodationAndGuest(accommodationId, guestId);
            var accommodationReview = accommodationReviewRepository.findByAccommodationAndGuest(accommodationId, guestId);

            ReviewDTOResponse review = ReviewDTOResponse.builder()
                    .guestName(r.getGuest().getFirstName() + " " + r.getGuest().getLastname())
                    .ownerReview(new ReviewDTO(ownerReview.get().getComment(), ownerReview.get().getRating()))
                    .accommodationReview(new ReviewDTO(accommodationReview.get().getComment(), accommodationReview.get().getRating()))
                    .build();

            reviews.add(review);
        }

        return convertListToPage(pageNo, pageSize, reviews);
    }


    private Page<ReviewDTOResponse> convertListToPage(int page, int size, List<ReviewDTOResponse> accommodationList){
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accommodationList.size());

        List<ReviewDTOResponse> pageContent;
        if(start > end) pageContent = new ArrayList<>();
        else pageContent = accommodationList.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, accommodationList.size());
    }
}
