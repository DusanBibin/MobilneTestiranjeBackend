package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    Optional<Accommodation> findAccommodationsByOwnerAndName(Owner owner, String name);

    Optional<Accommodation> findAccommodationById(Long accommodationId);


    @Query("select a from Accommodation a where a.owner.id = :ownerId")
    List<Accommodation> findByOwnerId(Long ownerId);


}
