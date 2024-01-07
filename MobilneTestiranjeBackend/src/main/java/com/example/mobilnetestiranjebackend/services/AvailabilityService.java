package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;

    public boolean reservationsNotEnded(Long accommodationId) {
        List<Reservation> reservationsNotEnded = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);
        return !reservationsNotEnded.isEmpty();
    }

    public Boolean availabilityRangeTaken(Long accommodationId, LocalDate startDate, LocalDate endDate){
        List<AccommodationAvailability> availabilitesSameRange = availabilityRepository.findAllByDateRange(accommodationId,startDate, endDate);
        return !availabilitesSameRange.isEmpty();
    }


}
