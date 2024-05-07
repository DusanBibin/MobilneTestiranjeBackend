package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.exceptions.ReservationNotEndedException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.GuestRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final ReservationService reservationService;
    private final AccommodationRepository accommodationRepository;
    private final GuestRepository guestRepository;

    public void deleteAccount(Owner owner) {

        var accommodations = accommodationRepository.findByOwnerId(owner.getId());
        for(Accommodation acc: accommodations){
            if(reservationService.reservationsNotEnded(acc.getId())) throw new ReservationNotEndedException("Unable to delete account as some of your reservations haven't ended");
        }


        for(Accommodation acc: accommodations){
            var guests = guestRepository.findByFavoriteAccommodationId(acc.getId());
            for(Guest guest: guests){
                guest.getFavorites().remove(acc);
            }
        }

        ownerRepository.delete(owner);

    }


}
