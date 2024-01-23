package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AccommodationRequestRepositoryTest {
    @Autowired
    private AccommodationRequestRepository accommodationRequestRepository;

    @Test
    void shouldReturnAccommodationRequestById() {
        AccommodationRequest accommodationRequest = new AccommodationRequest();
        accommodationRequestRepository.save(accommodationRequest);

        Optional<AccommodationRequest> result = accommodationRequestRepository.findById(accommodationRequest.getId());

        assertTrue(result.isPresent());
        assertEquals(accommodationRequest, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        Long nonExistentId = 123L;

        Optional<AccommodationRequest> result = accommodationRequestRepository.findById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldSaveAccommodationRequest() {
        AccommodationRequest accommodationRequest = new AccommodationRequest();
        AccommodationRequest savedUser = accommodationRequestRepository.save(accommodationRequest);
        accommodationRequestRepository.flush();

        assertThat(savedUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(accommodationRequest);
    }

}
