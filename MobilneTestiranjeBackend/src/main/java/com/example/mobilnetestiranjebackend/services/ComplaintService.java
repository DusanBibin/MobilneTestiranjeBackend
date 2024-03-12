package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ReviewComplaintRepository reviewComplaintRepository;
    private final GuestRepository guestRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final UserComplaintRepository userComplaintRepository;
    private final ReservationRepository reservationRepository;

    public void createReviewComplaint(Long ownerId, Long reviewId, String reason) {

        var ownerWrapper = ownerRepository.findById(ownerId);
        if(ownerWrapper.isEmpty()) throw new NonExistingEntityException("Owner with this id doesn't exist");
        var owner = ownerWrapper.get();

        AccommodationReview accommodationReview = null;
        OwnerReview ownerReview = null;
        boolean isAccommodationReview = true;


        Optional<AccommodationReview> accommodationReviewOptional = accommodationReviewRepository.findByReviewIdAndOwnerId(reviewId, ownerId);
        if (accommodationReviewOptional.isPresent()) {
            accommodationReview = accommodationReviewOptional.get();
        }
        Optional<OwnerReview> ownerReviewOptional = ownerReviewRepository.findByReviewIdAndOwnerId(reviewId, ownerId);
        if (ownerReviewOptional.isPresent()) {
            ownerReview = ownerReviewOptional.get();
            isAccommodationReview = false;
        }


        if(accommodationReview == null && ownerReview == null) throw new NonExistingEntityException("Review with this id doesn't exist");


        Guest guest;
        Review review;
        if(isAccommodationReview){
            guest = accommodationReview.getGuest();
            review = accommodationReview;
        }else{
            guest = ownerReview.getGuest();
            review = ownerReview;
        }


        var complaints = reviewComplaintRepository.findPendingByReviewId(reviewId);
        if(!complaints.isEmpty()) throw new InvalidAuthorizationException("You can only have 1 pending complaint");



        var reviewComplaint = ReviewComplaint.builder()
                .reason(reason)
                .status(RequestStatus.PENDING)
                .response("")
                .owner(owner)
                .review(review)
                .guest(guest)
                .build();

        reviewComplaint = reviewComplaintRepository.save(reviewComplaint);

        if(isAccommodationReview){
            accommodationReview.setComplaint(reviewComplaint);
            accommodationReview = accommodationReviewRepository.save(accommodationReview);
        }else{
            ownerReview.setComplaint(reviewComplaint);
            ownerReview = ownerReviewRepository.save(ownerReview);
        }


        owner.getReviewComplaints().add(reviewComplaint);
        owner = ownerRepository.save(owner);

        guest.getReviewComplaints().add(reviewComplaint);
        guest = guestRepository.save(guest);

    }

    public void createUserComplaint(Long reporterId, Long reportedId, String reason) {

        Owner owner = null;
        Guest guest = null;

        User reporter = null;
        User reported = null;

        var ownerWrapper = ownerRepository.findOwnerById(reporterId);
        if(ownerWrapper.isPresent()){

            owner = ownerWrapper.get();
            reporter = owner;

            var guestWrapper = guestRepository.findGuestById(reportedId);
            if(guestWrapper.isPresent()){
                guest = guestWrapper.get();
                reported = guest;
            }
        }


        ownerWrapper = ownerRepository.findOwnerById(reportedId);
        if(ownerWrapper.isPresent()){

            owner = ownerWrapper.get();
            reported = owner;

            var guestWrapper = guestRepository.findGuestById(reporterId);
            if(guestWrapper.isPresent()){
                guest = guestWrapper.get();
                reporter = guest;
            }
        }




        if(owner == null || guest == null) throw new NonExistingEntityException("User with this id doesn't exist");



        var completedReservations = reservationRepository.findGuestCompletedReservations(owner.getId(), guest.getId());
        if(completedReservations.isEmpty()) throw new InvalidAuthorizationException("No reservations found between the reporter and reported users");




        var pendingComplaints = userComplaintRepository.findByReporterIdAndReportedId(reporterId, reportedId);
        if(!pendingComplaints.isEmpty()) throw new InvalidAuthorizationException("There already exists a pending complaint");


        var userComplaint = UserComplaint.builder()
                .reporter(reporter)
                .reported(reported)
                .status(RequestStatus.PENDING)
                .reason(reason)
                .response("")
                .build();


        userComplaint = userComplaintRepository.save(userComplaint);


    }
}
