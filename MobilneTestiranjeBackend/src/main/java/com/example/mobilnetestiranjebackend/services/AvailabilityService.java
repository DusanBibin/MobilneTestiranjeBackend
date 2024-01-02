package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.EntityAlreadyExistsException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.exceptions.ReservationNotEndedException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRequestRepository;
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
    private final AccommodationRepository accommodationRepository;
    private final AvailabilityRequestRepository availRequestRepository;
    private final ReservationRepository reservationRepository;
    public void createAvailability(Integer id, AccommodationAvailabilityDTO availabilityDTO, Long accommodationId) {


        Optional<Accommodation> accommWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation does not exist");
        Accommodation accommodation = accommWrapper.get();

        if(availabilityRangeTaken(accommodationId,availabilityDTO.getStartDate(), availabilityDTO.getEndDate()))
            throw new EntityAlreadyExistsException("Availability in this date range already exists");

        if(availabilityDTO.getEndDate().isBefore(availabilityDTO.getStartDate()))
            throw new InvalidDateException("End date cannot be before start date");
        if(availabilityDTO.getCancellationDeadline().isAfter(availabilityDTO.getStartDate()) ||
                availabilityDTO.getCancellationDeadline().isEqual(availabilityDTO.getStartDate()))
            throw new InvalidDateException("Cancellation date cannot be after start date");
//
//        if(reservationsNotEnded(accommodationId))
//            throw new ReservationNotEndedException("One or more reservations for this date range haven't ended");\


        var accommodationAvailability = AvailabilityRequest.builder()
                .startDate(availabilityDTO.getStartDate())
                .endDate(availabilityDTO.getEndDate())
                .cancelDeadline(availabilityDTO.getCancellationDeadline())
                .price(availabilityDTO.getPrice())
                .pricePerGuest(availabilityDTO.getPricePerGuest())
                .accommodation(accommodation)
                .status(RequestStatus.PENDING)
                .isEditRequest(false)
                .build();

        availRequestRepository.save(accommodationAvailability);

    }

    private boolean reservationsNotEnded(Long accommodationId) {
        List<Reservation> reservationsNotEnded = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);
        return !reservationsNotEnded.isEmpty();
    }

    private Boolean availabilityRangeTaken(Long accommodationId, LocalDate startDate, LocalDate endDate){
        List<AccommodationAvailability> availabilitesSameRange = availabilityRepository.findAllByDateRange(accommodationId,startDate, endDate);
        return !availabilitesSameRange.isEmpty();
    }


}
