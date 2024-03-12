package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.UserComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserComplaintRepository extends JpaRepository<UserComplaint, Long> {

    @Query("select uc from UserComplaint uc where uc.reporter.id = :reporterId and uc.reported.id = :reportedId and uc.status = 0")
    List<UserComplaint> findByReporterIdAndReportedId(Long reporterId, Long reportedId);

}
