package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.OwnerReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerReviewRepository  extends JpaRepository<OwnerReview, Long> {
}
