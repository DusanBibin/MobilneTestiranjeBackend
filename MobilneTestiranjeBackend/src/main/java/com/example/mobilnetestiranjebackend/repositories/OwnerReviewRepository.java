package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationReview;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.OwnerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OwnerReviewRepository  extends JpaRepository<OwnerReview, Long> {

    @Query("select or from OwnerReview or where or.guest.id = :guestId and or.owner.id = :ownerId")
    List<OwnerReview> findOwnerReviewsByGuestId(Long guestId, Long ownerId);


    @Query("select or from OwnerReview or where or.id = :reviewId and or.guest.id = :guestId")
    Optional<OwnerReview> findByReviewIdAndGuestId(Long reviewId, Long guestId);

    @Query("select or from OwnerReview or where or.id = :reviewId and or.owner.id = :ownerId")
    Optional<OwnerReview> findByReviewIdAndOwnerId(Long reviewId, Long ownerId);


    @Query("select or from OwnerReview or join Accommodation a where a.id = :accommodationId")
    List<OwnerReview> findByAccommodationId(Long accommodationId);


    @Query("select or from OwnerReview or join Accommodation a where or.guest.id = :guestId and a.id = :accommodationId")
    Optional<OwnerReview> findByAccommodationAndGuest(Long accommodationId, Long guestId);


    @Query("select or from OwnerReview or where or.guest.id = :guestId and or.owner.id = :ownerId")
    Optional<OwnerReview> findByOwnerIdAndGuestId(Long ownerId, Long guestId);
}
