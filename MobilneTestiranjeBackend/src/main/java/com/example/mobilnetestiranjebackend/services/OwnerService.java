package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.exceptions.ReservationNotEndedException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final ReservationService reservationService;
    private final AccommodationRepository accommodationRepository;
    public void deleteAccount(Owner owner) {

        var accommodations = accommodationRepository.findByOwnerId(owner.getId());
        for(Accommodation acc: accommodations){
            if(reservationService.reservationsNotEnded(acc.getId())) throw new ReservationNotEndedException("Some reservations haven't ended");
        }

        ownerRepository.delete(owner);

    }


}
