package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.ReviewComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewComplaintRepository extends JpaRepository<ReviewComplaint, Long> {

    @Query("select rc from ReviewComplaint rc where rc.review.id = :reviewId and rc.status = 0")
    List<ReviewComplaint> findPendingByReviewId(Long reviewId);
}
