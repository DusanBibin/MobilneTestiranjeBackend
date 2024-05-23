package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {


    @Query("select ar from AccommodationReview ar where ar.guest.id = :guestId and ar.reservation.id = :reservationId")
    List<AccommodationReview> findByReservationIdAndGuestId(Long reservationId, Long guestId);

    @Query("select ar from AccommodationReview ar where ar.accommodation.id = :accommodationId")
    List<AccommodationReview> findByAccommodationId(Long accommodationId);

    @Query("select ar from AccommodationReview ar where ar.id = :reviewId and ar.guest.id = :guestId")
    Optional<AccommodationReview> findByReviewIdAndGuestId(Long reviewId, Long guestId);


    @Query("select ar from AccommodationReview ar where ar.id = :reviewId and ar.accommodation.owner.id = :ownerId")
    Optional<AccommodationReview> findByReviewIdAndOwnerId(Long reviewId, Long ownerId);


}
