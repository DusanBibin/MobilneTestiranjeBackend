package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.repositories.AccommodationReviewRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OwnerReviewRepository ownerReviewRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;



}
