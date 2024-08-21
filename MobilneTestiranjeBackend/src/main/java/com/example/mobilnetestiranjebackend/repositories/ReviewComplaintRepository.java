package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.ReviewComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewComplaintRepository extends JpaRepository<ReviewComplaint, Long> {

    @Query("select rc from ReviewComplaint rc where rc.review.id = :reviewId and rc.status = 0")
    List<ReviewComplaint> findPendingByReviewId(Long reviewId);

    @Query("select rc from ReviewComplaint rc where rc.id = :complaintId")
    Optional<ReviewComplaint> findComplaintById(Long complaintId);


    @Query("select rc from ReviewComplaint rc where rc.id = :complaintId and rc.owner.id = :ownerId")
    Optional<ReviewComplaint> findComplaintByIdAndOwnerId(Long complaintId, Long ownerId);


    @Query("select rc from ReviewComplaint rc where rc.review.id = :reviewId")
    Optional<ReviewComplaint> findByReviewId(Long reviewId);
}
