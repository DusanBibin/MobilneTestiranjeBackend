package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import org.junit.jupiter.api.Test;


@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class ReservationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;


    @Test
    public void shouldBeNull(){
        List<Accommodation> accommodations = accommodationRepository.findAll();
        System.out.println(accommodations.size());
    }

}
