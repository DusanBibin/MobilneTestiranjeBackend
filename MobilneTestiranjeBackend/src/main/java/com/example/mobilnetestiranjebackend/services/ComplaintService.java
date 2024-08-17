package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.InvalidIsolationLevelException;

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
    private final UserRepository userRepository;

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

    public void createUserComplaint(Long reporterId, Long reservationId, String reason, Role role) {

        Owner owner = null;
        Guest guest = null;

        User reporter = null;
        User reported = null;

        Long reportedId = null;

        if(role.equals(Role.OWNER)){
            reportedId = reservationRepository.findById(reservationId).get().getGuest().getId();
        }else{
            reportedId = reservationRepository.findById(reservationId).get().getAccommodation().getOwner().getId();
        }


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

    public void reviewReviewComplaint(Long complaintId, String response, RequestStatus status) {

        var reviewComplaintWrapper = reviewComplaintRepository.findById(complaintId);
        if(reviewComplaintWrapper.isEmpty()) throw new NonExistingEntityException("Review with this id doesn't exist");
        var reviewComplaint = reviewComplaintWrapper.get();


        if(!reviewComplaint.getStatus().equals(RequestStatus.PENDING))
            throw new InvalidAuthorizationException("You cannot already review already reviewed complaint");

        reviewComplaint.setStatus(status);
        reviewComplaint.setResponse(response);
        reviewComplaint = reviewComplaintRepository.save(reviewComplaint);

    }


    public void reviewUserComplaint(Long complaintId, String response, RequestStatus status) {

        var userComplaintWrapper = userComplaintRepository.findById(complaintId);
        if(userComplaintWrapper.isEmpty()) throw new NonExistingEntityException("Review with this id doesn't exist");
        var userComplaint = userComplaintWrapper.get();


        if(!userComplaint.getStatus().equals(RequestStatus.PENDING))
            throw new InvalidAuthorizationException("You cannot already review already reviewed complaint");
        
        var userWrapper = userRepository.findByUserId(userComplaint.getReported().getId());
        if(userWrapper.isEmpty()) throw new NonExistingEntityException("User with this id doesn't exist");
        var user = userWrapper.get();

        if(user.getBlocked()) throw new InvalidIsolationLevelException("User is already blocked");


        if(status.equals(RequestStatus.ACCEPTED)){

            user.setBlocked(true);
            user = userRepository.save(user);

            if(user instanceof Guest){
                Guest guest = (Guest) user;

                for(Reservation reservation: guest.getReservations()){
                    reservation.setStatus(ReservationStatus.CANCELED);
                    reservation = reservationRepository.save(reservation);
                }
            }
        }


        userComplaint.setStatus(status);
        userComplaint.setResponse(response);
        userComplaint = userComplaintRepository.save(userComplaint);
        
    }




}
