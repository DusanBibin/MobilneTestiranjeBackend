package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTOResponse;
import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReviewDTOResponse;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OwnerRepository ownerRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final ReviewComplaintRepository reviewComplaintRepository;
    private final NotificationService notificationService;

    public ReviewDTO createOwnerReview(ReviewDTO reviewDTO, Long ownerId, Long guestId) {

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
                .allowed(true)
                .comment(reviewDTO.getComment())
                .rating(reviewDTO.getRating())
                .build();


        ownerReview = ownerReviewRepository.save(ownerReview);

        guest.getOwnerReviews().add(ownerReview);
        guestRepository.save(guest);

        owner.getOwnerReviews().add(ownerReview);
        ownerRepository.save(owner);
        reviewDTO.setReviewId(ownerReview.getId());
        notificationService.createNotification(ownerId, "You have new review", NotificationType.OWNER_REVIEW);
        return reviewDTO;
    }

    public void deleteOwnerReview(Long reviewId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");


        var reviewWrapper = ownerReviewRepository.findByReviewIdAndGuestId(reviewId, guestId);
        if(reviewWrapper.isEmpty()) throw new NonExistingEntityException("Owner review with this id doesn't exist");
        var review = reviewWrapper.get();

        ownerReviewRepository.delete(review);

    }

    public ReviewDTO createAccommodationReview(ReviewDTO reviewDTO, Long accommodationId, Long reservationId, Long guestId) {

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
                .allowed(true)
                .comment(reviewDTO.getComment())
                .rating(reviewDTO.getRating())
                .guest(guest)
                .reservation(reservation)
                .accommodation(accommodation)
                .build();

        accommodationReview = accommodationReviewRepository.save(accommodationReview);

        accommodation.getAccommodationReviews().add(accommodationReview);
        accommodationRepository.save(accommodation);

        reviewDTO.setReviewId(accommodationReview.getId());
        notificationService.createNotification(accommodation.getOwner().getId(), "You have a new accomodation review", NotificationType.ACCOMMODATION_REVIEW);

        return reviewDTO;
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


            if(accommodationReview.isPresent() || ownerReview.isPresent()){
                ReviewDTOResponse review = ReviewDTOResponse.builder()
                        .guestName(r.getGuest().getFirstName() + " " + r.getGuest().getLastname())
                        .build();

                if(ownerReview.isPresent()){
                    OwnerReview or = ownerReview.get();
                    if(or.getAllowed()){
                        review.setOwnerReview(new ReviewDTO(0L, or.getComment(), or.getRating(), null, 0L, null, null));
                    }
                }

                if(accommodationReview.isPresent()){
                    AccommodationReview ar = accommodationReview.get();
                    if(ar.getAllowed()){
                        review.setAccommodationReview(new ReviewDTO(0L, ar.getComment(), ar.getRating(), null, 0L, null, null));
                    }
                }
                reviews.add(review);
            }
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

    public ReviewDTOResponse getReview(Long accommodationId, Long reservationId, Long userId) {

        Optional<User> userWrapper = userRepository.findByUserId(userId);
        if(userWrapper.isEmpty()) throw new InvalidAuthorizationException("This user doesn't exist");
        User user = userWrapper.get();

        Optional<Reservation> reservationWrapper = reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new InvalidInputException("This reservation does not exist");
        Reservation reservation = reservationWrapper.get();

        if(user instanceof Owner && !reservation.getAccommodation().getOwner().getId().equals(user.getId())) throw new InvalidAuthorizationException("You are not the owner in this reservation");
        else if(user instanceof Guest && !reservation.getGuest().getId().equals(user.getId())) throw new InvalidAuthorizationException("You are not the guest in this reservation");


        Guest guest = reservation.getGuest();

        Optional<Accommodation> accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        Accommodation accommodation = accommodationWrapper.get();
        Owner owner = accommodation.getOwner();


        Optional<OwnerReview> orWrapper;
        Optional<AccommodationReview> arWrapper;

        orWrapper = ownerReviewRepository.findByOwnerIdAndGuestId(owner.getId(), guest.getId());
        arWrapper = accommodationReviewRepository.findByAccommodationIdAndGuestId(accommodationId, guest.getId());


        ReviewDTO orDTO = null, arDTO = null;
        if(orWrapper.isPresent()){
            OwnerReview or = orWrapper.get();

            Optional<ReviewComplaint> orc = reviewComplaintRepository.findByReviewId(or.getId());
            String reason = null;
            Long complaintId = 0L;
            String adminResponse = null;
            RequestStatus status = null;
            if(orc.isPresent()){
                reason = orc.get().getReason();
                complaintId = orc.get().getId();
                adminResponse = orc.get().getResponse();
                if(!orc.get().getStatus().equals(RequestStatus.PENDING)) adminResponse = orc.get().getResponse();
                status = orc.get().getStatus();
            }

            orDTO = new ReviewDTO(or.getId(), or.getComment(), or.getRating(), reason, complaintId, adminResponse, status);
        }
        if(arWrapper.isPresent()){
            AccommodationReview ar = arWrapper.get();


            Optional<ReviewComplaint> orc = reviewComplaintRepository.findByReviewId(ar.getId());
            String reason = null;
            Long complaintId = 0L;
            String adminResponse = null;
            RequestStatus status = null;
            if(orc.isPresent()){
                reason = orc.get().getReason();
                complaintId = orc.get().getId();
                if(!orc.get().getStatus().equals(RequestStatus.PENDING)) adminResponse = orc.get().getResponse();
                status = orc.get().getStatus();
            }

            arDTO = new ReviewDTO(ar.getId(), ar.getComment(), ar.getRating(), reason, complaintId, adminResponse, status);
        }


        ReviewDTOResponse review = new ReviewDTOResponse();
        review.setOwnerReview(orDTO);
        review.setAccommodationReview(arDTO);
        review.setGuestName(guest.getFirstName() + " " + guest.getLastname());


        return review;
    }

    public Double getAverageAccommodationRating(Long accommodationId) {
        List<AccommodationReview> reviews = accommodationReviewRepository.findByAccommodationId(accommodationId);
        return reviews.stream().filter(Review::getAllowed).mapToLong(Review::getRating).average().orElse(0.0);
    }

    public Double getAverageOwnerRating(Long ownerId) {
        List<OwnerReview> reviews = ownerReviewRepository.findByOwnerId(ownerId);
        return reviews.stream().filter(Review::getAllowed).mapToLong(Review::getRating).average().orElse(0.0);
    }
}
