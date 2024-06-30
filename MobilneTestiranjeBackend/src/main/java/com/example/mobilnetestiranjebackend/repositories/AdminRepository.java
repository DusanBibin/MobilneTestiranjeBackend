package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Admin;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query("select a from Admin a where a.id = :id")
    Optional<Admin> findByAdminId(Long id);
}
