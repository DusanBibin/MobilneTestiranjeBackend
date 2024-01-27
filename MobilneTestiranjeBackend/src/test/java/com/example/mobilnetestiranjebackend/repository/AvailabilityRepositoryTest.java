package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AvailabilityRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    public void testFindByIdAndAccommodationId_Success() {

        var accommodation = Accommodation.builder()
                .availabilityList(new ArrayList<>())
                .build();
        accommodation = accommodationRepository.save(accommodation);


        var availability = Availability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        Optional<Availability> availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(availability.getId(), accommodation.getId());

        assertThat(availabilityWrapper).isPresent();
    }

    @Test
    public void testFindByIdAndAccommodationId_NonExistingAvailabilityId() {

        var accommodation = Accommodation.builder()
                .availabilityList(new ArrayList<>())
                .build();
        accommodation = accommodationRepository.save(accommodation);


        var availability = Availability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        Optional<Availability> availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(0L, accommodation.getId());

        assertThat(availabilityWrapper).isEmpty();
    }

    @Test
    public void testFindByIdAndAccommodationId_NonExistingAccommodationId() {

        var accommodation = Accommodation.builder()
                .availabilityList(new ArrayList<>())
                .build();
        accommodation = accommodationRepository.save(accommodation);


        var availability = Availability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        Optional<Availability> availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(accommodation.getId(), 0L);

        assertThat(availabilityWrapper).isEmpty();
    }
}

