package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class AccommodationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    @Test
    void shouldReturnAccommodationByNameAndOwner() {
        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();

        String name = "Test Name";
        Accommodation accommodation = new Accommodation();
        accommodation.setOwner(owner);
        accommodation.setName(name);
        accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(owner, name);

        assertTrue(result.isPresent());
        assertEquals(accommodation, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentNameAndOwner() {

        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();
        String name = "Wrong Name";

        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(owner, name);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalForWrongName() {
        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();

        String name = "Test Name";
        Accommodation accommodation = new Accommodation();
        accommodation.setOwner(owner);
        accommodation.setName(name);
        accommodationRepository.save(accommodation);
        accommodationRepository.flush();
        String wrongName = "Wrong Name";

        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(owner, wrongName);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalForWrongOwner() {

        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();

        String name = "Test Name";
        Accommodation accommodation = new Accommodation();
        accommodation.setOwner(owner);
        accommodation.setName(name);
        accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        Owner owner1 = new Owner();
        ownerRepository.save(owner1);
        ownerRepository.flush();

        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(owner1, name);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalForNullName() {
        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();

        String name = "Test Name";
        Accommodation accommodation = new Accommodation();
        accommodation.setOwner(owner);
        accommodation.setName(name);
        accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(owner, null);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalForNullOwner() {

        Owner owner = new Owner();
        ownerRepository.save(owner);
        ownerRepository.flush();

        String name = "Test Name";
        Accommodation accommodation = new Accommodation();
        accommodation.setOwner(owner);
        accommodation.setName(name);
        accommodationRepository.save(accommodation);
        accommodationRepository.flush();


        Optional<Accommodation> result = accommodationRepository.findAccommodationsByOwnerAndName(null, name);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnAccommodationById() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);

        Optional<Accommodation> result = accommodationRepository.findAccommodationById(accommodation.getId());

        assertTrue(result.isPresent());
        assertEquals(accommodation, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        Long nonExistentId = 123L;

        Optional<Accommodation> result = accommodationRepository.findAccommodationById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldSaveAccommodation() {
        Accommodation accommodation = new Accommodation();
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        assertThat(savedAccommodation).usingRecursiveComparison().ignoringFields("id").isEqualTo(accommodation);
    }

}
