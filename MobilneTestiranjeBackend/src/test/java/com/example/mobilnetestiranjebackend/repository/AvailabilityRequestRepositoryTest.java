package com.example.mobilnetestiranjebackend.repository;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class AvailabilityRequestRepositoryTest {

    @Autowired
    private AvailabilityRequestRepository availabilityRequestRepository;

    @Test
    public void shouldSaveAvailability() {
        AvailabilityRequest accommodation = new AvailabilityRequest();
        AvailabilityRequest savedAccommodationAvailability = availabilityRequestRepository.save(accommodation);
        availabilityRequestRepository.flush();

        assertThat(savedAccommodationAvailability).usingRecursiveComparison().ignoringFields("id").isEqualTo(accommodation);
    }
}
