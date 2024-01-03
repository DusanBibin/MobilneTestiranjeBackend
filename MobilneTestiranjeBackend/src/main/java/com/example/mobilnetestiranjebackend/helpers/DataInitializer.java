package com.example.mobilnetestiranjebackend.helpers;

import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccommodationRepository accommodationRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    public void run(String... args) throws Exception {
        Owner ownerDusan = Owner.builder()
                .firstName("Dusan")
                .lastname("Bibin")
                .email("dusanbibin2@gmail.com")
                .password(passwordEncoder.encode("NekaSifra123"))
                .phoneNumber("0691817839")
                .address("Neka ulica 123")
                .emailConfirmed(true)
                .blocked(false)
                .role(Role.OWNER)
                .accommodationRequests(new ArrayList<>())
                .accommodations(new ArrayList<>())
                .build();
        

        ownerRepository.save(ownerDusan);
    }
}
