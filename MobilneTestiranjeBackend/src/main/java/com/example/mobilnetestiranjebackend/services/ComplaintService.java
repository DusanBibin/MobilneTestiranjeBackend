package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.ComplaintDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserComplaintDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.helpers.PageConverter;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.InvalidIsolationLevelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ComplaintDTO createReviewComplaint(Long ownerId, Long reviewId, String reason) {

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

        Long reservationId = 0L;
        String reviewType = "Owner";
        if(isAccommodationReview){
            reservationId = accommodationReview.getReservation().getId();
            reviewType = "accommodation";
        }

        return ComplaintDTO.builder()
                .ownerNameSurname(owner.getFirstName() + " " + owner.getLastname())
                .ownerEmail(owner.getEmail())
                .guestNameSurname(guest.getFirstName() + " " + guest.getLastname())
                .guestEmail(guest.getEmail())
                .reviewRating(review.getRating())
                .reviewComment(review.getComment())
                .complaintReason(reason)
                .requestStatus(RequestStatus.PENDING)
                .reviewType(reviewType)
                .adminResponse(null)
                .build();
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

    public void reviewCommentComplaint(Long complaintId, String response, RequestStatus status) {

        var reviewComplaintWrapper = reviewComplaintRepository.findById(complaintId);
        if(reviewComplaintWrapper.isEmpty()) throw new NonExistingEntityException("Review with this id doesn't exist");
        var reviewComplaint = reviewComplaintWrapper.get();


        if(!reviewComplaint.getStatus().equals(RequestStatus.PENDING))
            throw new InvalidAuthorizationException("You cannot already review already reviewed complaint");
        reviewComplaint.getReview().setAllowed(false);
        reviewComplaint.setStatus(status);
        reviewComplaint.setResponse(response);
        reviewComplaint = reviewComplaintRepository.save(reviewComplaint);

    }


    public void reviewUserComplaint(Long complaintId, RequestStatus status) {

        var userComplaintWrapper = userComplaintRepository.findById(complaintId);
        if(userComplaintWrapper.isEmpty()) throw new NonExistingEntityException("Review with this id doesn't exist");
        UserComplaint userComplaint = userComplaintWrapper.get();

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
        userComplaint = userComplaintRepository.save(userComplaint);

    }

    public Page<UserComplaintDTO> getUserComplaints(int pageNo, int pageSize) {
        List<UserComplaint> userComplaints = userComplaintRepository.findPendingUserComplaints();
        List<UserComplaintDTO> complaintDTOs = userComplaints.stream().map(uc -> {
            UserComplaintDTO dto = new UserComplaintDTO();
            dto.setId(uc.getId());
            dto.setReporedUser(uc.getReported().getUsername());
            dto.setReporterUser(uc.getReporter().getUsername());
            dto.setReportedUserRole(uc.getReported().getRole().name());
            dto.setReporterUserRole(uc.getReporter().getRole().name());
            dto.setReason(uc.getReason());
            return dto;
        }).collect(Collectors.toList());
        return PageConverter.convertListToPage(pageNo, pageSize, complaintDTOs);
    }


    public ComplaintDTO getComplaint(Long complaintId, Long userId) {

        var userWrapper = userRepository.findByUserId(userId);
        if(userWrapper.isEmpty()) throw new NonExistingEntityException("User with this id doesn't exist");
        User user = userWrapper.get();

        Optional<ReviewComplaint> reviewComplaintWrapper = reviewComplaintRepository.findComplaintById(complaintId);
        if(reviewComplaintWrapper.isEmpty()) throw new NonExistingEntityException("Review with this id doesn't exist");
        ReviewComplaint complaint = reviewComplaintWrapper.get();
        Owner owner = complaint.getOwner();
        Guest guest = complaint.getGuest();
        Review review = complaint.getReview();

        if(user instanceof Owner && reviewComplaintRepository.findComplaintByIdAndOwnerId(complaintId, userId).isEmpty())
            throw new InvalidAuthorizationException("You do not own this complaint");


        return ComplaintDTO.builder()
                .ownerNameSurname(owner.getFirstName() + " " + owner.getLastname())
                .ownerEmail(owner.getEmail())
                .guestNameSurname(guest.getFirstName() + " " + guest.getLastname())
                .guestEmail(guest.getEmail())
                .reviewRating(review.getRating())
                .reviewComment(review.getComment())
                .complaintReason(complaint.getReason())
                .requestStatus(complaint.getStatus())
                .adminResponse(complaint.getResponse())
                .build();
    }

    public Page<ComplaintDTO> getComplaints(Long adminId, int pageNo, int pageSize) {
        Optional<User> userWrapper = userRepository.findByUserId(adminId);
        if(userWrapper.isEmpty()) throw new NonExistingEntityException("User with this id doesn't exist");
        User user = userWrapper.get();
        if(!(user instanceof Admin)) throw new InvalidAuthorizationException("You do not have authorization for this");

        List<ReviewComplaint> reviewComplaints = reviewComplaintRepository.findAll();

        List<ComplaintDTO> convertedList = new ArrayList<>(reviewComplaints.stream().map(a -> {
            ComplaintDTO dto = new ComplaintDTO();

            Review review = a.getReview();
            Long accommodationId = 0L, reservationId = 0L;
            if(review instanceof AccommodationReview){
                Optional<AccommodationReview> ar = accommodationReviewRepository.findById(review.getId());
                if(ar.isEmpty()) throw new InvalidInputException("Accommodation review with this id doesn't exist");
                AccommodationReview arReview = ar.get();
                accommodationId = arReview.getAccommodation().getId();
                reservationId = arReview.getReservation().getId();
            }

            dto.setRequestStatus(a.getStatus());
            dto.setOwnerNameSurname(a.getOwner().getFirstName() + " " + a.getOwner().getLastname());
            dto.setOwnerEmail(a.getOwner().getEmail());
            dto.setComplaintId(a.getId());
            dto.setAccommodationId(accommodationId);
            dto.setReservationId(reservationId);
            dto.setReviewId(a.getReview().getId());
            String reviewType = "Accommodation";
            if(a.getReview() instanceof OwnerReview)  reviewType = "Owner";
            dto.setReviewType(reviewType);
            return dto;
        }).toList());


        return PageConverter.convertListToPage(pageNo, pageSize, convertedList);
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
}
