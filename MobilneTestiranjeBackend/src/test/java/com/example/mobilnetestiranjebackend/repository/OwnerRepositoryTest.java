package com.example.mobilnetestiranjebackend.repository;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository ownerRepository;


    @Test
    void shouldReturnOwnerById() {
        Owner owner = new Owner();
        owner = ownerRepository.save(owner);
        ownerRepository.flush();

        Optional<Owner> result = ownerRepository.findOwnerById(owner.getId());

        assertTrue(result.isPresent());
        assertEquals(owner, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        Long nonExistentId = 123L;

        Optional<Owner> result = ownerRepository.findOwnerById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldSaveOwner() {
        Owner owner = new Owner();
        Owner savedOwner = ownerRepository.save(owner);
        ownerRepository.flush();

        assertThat(savedOwner).usingRecursiveComparison().ignoringFields("id").isEqualTo(owner);
    }
}
