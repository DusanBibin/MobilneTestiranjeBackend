package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final GuestRepository guestRepository;

    public Optional<Accommodation> findAccommodationById(Long accommodationId) {
        return accommodationRepository.findAccommodationById(accommodationId);
    }


    public void addToFavorites(Long accommodationId, Long guestId) {


        var guestWrapper = guestRepository.findGuestById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        var accommodation = accommodationWrapper.get();


        var favoriteAccommodationWrapper = accommodationRepository.findFavoritesByAccommodationIdAndGuestId(accommodationId, guestId);
        if(favoriteAccommodationWrapper.isPresent()) throw new InvalidAuthorizationException("You already have this accommodation in favorites");


        guest.getFavorites().add(accommodation);
        guestRepository.save(guest);

    }

    public void removeFromFavorites(Long accommodationId, Long guestId) {

        var guestWrapper = guestRepository.findGuestById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");

        var favoriteAccommodationWrapper = accommodationRepository.findFavoritesByAccommodationIdAndGuestId(accommodationId, guestId);
        if(favoriteAccommodationWrapper.isEmpty()) throw new InvalidAuthorizationException("This id doesn't exist in your favorites");
        var favoriteAccommodation = favoriteAccommodationWrapper.get();



        guest.getFavorites().remove(favoriteAccommodation);
        guestRepository.save(guest);


    }

}

