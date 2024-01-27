package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;

    public Boolean reservationsNotEnded(Long accommodationId) {
        List<Reservation> reservationsNotEnded = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);
        return !reservationsNotEnded.isEmpty();
    }

    public Boolean availabilityRangeTaken(Long accommodationId, LocalDate startDate, LocalDate endDate, Long newAvailId){
        List<Availability> availabilitesSameRange = availabilityRepository.findAllByDateRange(accommodationId,startDate, endDate, newAvailId);
        return !availabilitesSameRange.isEmpty();
    }

    public Optional<Availability> findAvailabilityById(Long availabilityId){
        return availabilityRepository.findById(availabilityId);
    }


    public Optional<Availability> findAvailabilityByIdAndAccommodation(Long availabilityId, Long accommodationId) {




        return availabilityRepository.findByIdAndAccommodationId(availabilityId, accommodationId);
    }
}
