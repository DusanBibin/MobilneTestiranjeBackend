package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.OwnerReview;
import com.example.mobilnetestiranjebackend.repositories.GuestRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerReviewService {

    private final OwnerRepository ownerRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final GuestRepository guestRepository;

    public void createOwnerReview(ReviewDTO reviewDTO, Long ownerId, Long guestId) {

        var guestWrapper = guestRepository.findById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        if(ownerWrapper.isEmpty()) throw new NonExistingEntityException("Owner with this id doesn't exist");
        var owner = ownerWrapper.get();

        System.out.println(" guest email je :" + guest.getEmail());

        System.out.println(" owner email je :" + owner.getEmail());
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
        var guest = guestWrapper.get();

        var ownerWrapper = ownerRepository.findById(1L);
        if(ownerWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var owner = ownerWrapper.get();

        var reviewWrapper = ownerReviewRepository.findById(reviewId);
        if(reviewWrapper.isEmpty()) throw new NonExistingEntityException("Owner review with this id doesn't exist");
        var review = reviewWrapper.get();

        ownerReviewRepository.delete(review);

    }

}
