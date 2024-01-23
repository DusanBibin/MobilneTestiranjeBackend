package com.example.mobilnetestiranjebackend.repository;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvailabilityRepositoryTest {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @AfterEach
    public void cleanup() {
        ArrayList<Accommodation> accs = (ArrayList<Accommodation>) accommodationRepository.findAll();
        accommodationRepository.deleteAll(accs);

        ArrayList<AccommodationAvailability> ress = (ArrayList<AccommodationAvailability>) availabilityRepository.findAll();
        availabilityRepository.deleteAll(ress);
    }

    @Test
    public void shouldSaveAvailability() {
        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        AccommodationAvailability savedAccommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        assertThat(savedAccommodationAvailability).usingRecursiveComparison().ignoringFields("id").isEqualTo(accommodationAvailability);
    }

    @Test
    void shouldReturnAccommodationAvailabilityById() {
        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);

        Optional<AccommodationAvailability> result = availabilityRepository.findById(accommodationAvailability.getId());

        assertTrue(result.isPresent());
        assertEquals(accommodationAvailability, result.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        Long nonExistentId = 123L;

        Optional<AccommodationAvailability> result = availabilityRepository.findById(nonExistentId);

        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    @Order(1)
    void shouldReturnAvailabilityByIdAndAccommodationId() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        Optional<AccommodationAvailability> result = availabilityRepository.findByIdAndAccommodationId(accommodationAvailability.getId(), accommodation.getId());
        assertTrue(result.isPresent());
        assertEquals(accommodationAvailability, result.get());
    }

    @Test
    void shouldNotReturnAvailabilityForWrongId() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        Optional<AccommodationAvailability> result = availabilityRepository.findByIdAndAccommodationId(accommodationAvailability2.getId(), accommodation.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotReturnAvailabilityForWrongAccommodationId() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        Accommodation accommodation2 = new Accommodation();
        accommodation2 = accommodationRepository.save(accommodation2);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        Optional<AccommodationAvailability> result = availabilityRepository.findByIdAndAccommodationId(accommodationAvailability.getId(), accommodation2.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotReturnAvailabilityForWrongIdAndAccommodationId() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        Accommodation accommodation2 = new Accommodation();
        accommodation2 = accommodationRepository.save(accommodation2);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        Optional<AccommodationAvailability> result = availabilityRepository.findByIdAndAccommodationId(accommodationAvailability2.getId(), accommodation2.getId());
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldThrowExceptionWhenSaveNullEntity() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> accommodationRepository.saveAndFlush(null));
    }

    @Test
    public void shouldThrowExceptionWhenEditNullEntity() {
        AccommodationAvailability accommodationAvailability = null;
        Accommodation accommodation = new Accommodation();
        assertThrows(NullPointerException.class, () -> accommodationAvailability.setAccommodation(accommodation));
    }

//    @Test
//    public void shouldThrowExceptionWhenAccessNullEntity() {
//        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
//        Accommodation accommodation = new Accommodation();
//        accommodation = accommodationRepository.saveAndFlush(accommodation);
//        accommodationAvailability.setAccommodation(null);
//        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
//        AccommodationAvailability finalAccommodationAvailability = accommodationAvailability;
//        Accommodation finalAccommodation = accommodation;
//        assertThrows(NullPointerException.class, () -> availabilityRepository.findByIdAndAccommodationId(finalAccommodationAvailability.getId(), finalAccommodation.getId()));
//    }

    @Test
    public void shouldFindAccommodationAvailabilityList() { // start and end dates between
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList2() { // end date between
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 9, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList3() { // start date between
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2027, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList4() { // start date before and end date after
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 9, 2), LocalDate.of(2027, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList5() { // start dates are equal
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList6() { // end dates are equal
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 11, 1), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList7() { // both dates are equal
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 1), LocalDate.of(2023, 11, 1), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList8() { // all dates are equal
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 10, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 1), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList9() { // start equals end
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 10, 2));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }

    @Test
    public void shouldFindAccommodationAvailabilityList10() { // end equals start
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 25));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertFalse(l.isEmpty());
    }


    @Test
    public void shouldNotFindAccommodationAvailabilityListIfAvailabilityIdIsTheSame() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfAccomodationIsNull() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(null);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfWrongAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);

        Accommodation accommodation2 = new Accommodation();
        accommodation2 = accommodationRepository.save(accommodation2);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation2.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfAvailabilityEndDateBeforeStartDate() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 11, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 10, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 10, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfRequestedEndDateBeforeStartDate() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 11, 2), LocalDate.of(2023, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfStartDateAfterAvailabilityEnd() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 12, 2), LocalDate.of(2023, 12, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfEndDateBeforeAvailabilityEnd() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 9, 2), LocalDate.of(2023, 9, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfDatesBeforeStart() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2023, 9, 2), LocalDate.of(2023, 9, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }

    @Test
    public void shouldNotFindAccommodationAvailabilityListIfDatesAfterEnd() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();

        AccommodationAvailability accommodationAvailability = new AccommodationAvailability();
        accommodationAvailability.setAccommodation(accommodation);
        accommodationAvailability.setStartDate(LocalDate.of(2023, 10, 1));
        accommodationAvailability.setEndDate(LocalDate.of(2023, 11, 1));

        accommodationAvailability = availabilityRepository.save(accommodationAvailability);
        availabilityRepository.flush();

        AccommodationAvailability accommodationAvailability2 = new AccommodationAvailability();
        accommodationAvailability2 = availabilityRepository.save(accommodationAvailability2);
        availabilityRepository.flush();

        List<AccommodationAvailability> l = availabilityRepository.findAllByDateRange(accommodation.getId(), LocalDate.of(2027, 10, 2), LocalDate.of(2027, 10, 25), accommodationAvailability2.getId());
        System.out.println(l.size());

        assertTrue(l.isEmpty());
    }


}
