package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.exceptions.ReservationNotEndedException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.GuestRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public void deleteAccount(Guest guest) {

        var notEndedReservations = reservationRepository.findReservationsNotEndedByGuestId(guest.getId());
        if(!notEndedReservations.isEmpty()) throw new ReservationNotEndedException("Some reservations haven't ended");

        guestRepository.delete(guest);
    }
}
