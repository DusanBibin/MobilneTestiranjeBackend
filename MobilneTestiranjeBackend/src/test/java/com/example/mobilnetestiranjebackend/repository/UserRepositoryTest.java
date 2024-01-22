package com.example.mobilnetestiranjebackend.repository;


import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.model.Verification;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        User user = new User(null, "Kristina", "Andrijin", "andrijinkristina@gmail.com", "123", "060", "Adresa", true, false, null, Role.OWNER);
        User savedUser = userRepository.save(user);
        userRepository.flush();

        assertThat(savedUser).usingRecursiveComparison().ignoringFields("userId").isEqualTo(user);
    }

    @Test
    public void fshouldSaveUsersThroughSqlFile() {
        User user = new User(null, "Kristina", "Andrijin", "andrijinkristina@gmail.com", "123", "060", "Adresa", true, false, null, Role.OWNER);
        User savedUser = userRepository.save(user);
        userRepository.flush();
        
        Optional<User> test = userRepository.findByEmail("andrijinkristina@gmail.com");
        assertThat(test).isNotEmpty();
    }
}
