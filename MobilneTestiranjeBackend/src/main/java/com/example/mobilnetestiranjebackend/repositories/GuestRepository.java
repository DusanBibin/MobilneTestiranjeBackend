package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {


    Optional<Guest> findGuestById(Long guestId);


    @Query("select g from Guest g join g.favorites f where f.id = :accommodationId")
    List<Guest> findByFavoriteAccommodationId(Long accommodationId);
}
