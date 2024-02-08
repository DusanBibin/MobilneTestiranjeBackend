package com.example.mobilnetestiranjebackend.services;


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
    private final AccommodationRepository accommodationRepository;

    public void deleteAccount(Owner owner) {


        var accommodations = owner.getAccommodations();
        System.out.println(accommodations.size());
        var accomRequests = owner.getAccommodationRequests();
        System.out.println(accomRequests.size());
        //ownerRepository.delete(owner);



    }
}
