package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findOwnerById(Long ownerId);
}
