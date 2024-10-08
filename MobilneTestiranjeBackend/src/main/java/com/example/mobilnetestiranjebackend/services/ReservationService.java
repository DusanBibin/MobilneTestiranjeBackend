package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationRequestPreviewDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.DTOs.MonthlyReportDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.helpers.PageConverter;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final OwnerRepository ownerRepository;
    private final GuestRepository guestRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final OwnerReviewRepository ownerReviewRepository;
    private final AdminRepository adminRepository;


    public Optional<Reservation> findReservationByIdAccommodation(Long accommodationId, Long reservationId) {
        return reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
    }


    public boolean acceptedReservationRangeTaken(LocalDate startDate, LocalDate endDate, Long accomId, Long availId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                 findAcceptedReservationsInConflict(startDate, endDate, accomId, availId);

        return !sameRangeReservations.isEmpty();
    }

    public boolean acceptedReservationRangeTakenGuest(LocalDate startDate, LocalDate endDate, Long guestId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                findGuestReservationsInConflict(startDate, endDate, guestId);

        return !sameRangeReservations.isEmpty();
    }

    public void createNewReservation(ReservationDTO request, Guest guest, Accommodation accom, Availability avail) {

        ReservationStatus status = ReservationStatus.PENDING;
        String reason = "";
        if(accom.getAutoAcceptEnabled()){
            status = ReservationStatus.ACCEPTED;
            reason = "ACCEPTED";
        }


        Long days = ChronoUnit.DAYS.between(request.getReservationStartDate(), request.getReservationEndDate()) + 1;

        Long totalPrice;
        if (avail.getPricePerGuest()) totalPrice = days * avail.getPrice() * request.getGuestNum();
        else totalPrice = days * avail.getPrice();

        var reservation = Reservation.builder()
                .reservationStartDate(request.getReservationStartDate())
                .reservationEndDate(request.getReservationEndDate())
                .cancelDeadline(avail.getCancelDeadline())
                .perGuest(avail.getPricePerGuest())
                .unitPrice(avail.getPrice())
                .guestNum(request.getGuestNum())
                .status(status)
                .reason(reason)
                .guest(guest)
                .accommodation(accom)
                .availability(avail)
                .price(totalPrice)
                .build();

        reservationRepository.save(reservation);

        accom.getReservations().add(reservation);
        accommodationRepository.save(accom);
    }

    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public Optional<Reservation> findReservationByIdAndGuest(Long reservationId, Long guestId){
        return reservationRepository.findByIdAndGuest(reservationId, guestId);
    }

    public void declineRequest(String reason, Reservation reservation) {

        if(!reservation.getStatus().equals(ReservationStatus.PENDING)) throw new InvalidEnumValueException("You can only decline a pending request");

        reservation.setStatus(ReservationStatus.DECLINED);
        reservation.setReason(reason);

        reservationRepository.save(reservation);
    }

    public void acceptRequest(Reservation reservation) {
        List<Reservation> conflictedReservations = reservationRepository
                .findPendingConflictedReservations(reservation.getAccommodation().getId(), reservation.getId(),
                        reservation.getReservationStartDate(), reservation.getReservationEndDate());

        for(Reservation conflictReservation: conflictedReservations){
            conflictReservation.setReason("Other reservation was accepted");
            conflictReservation.setStatus(ReservationStatus.DECLINED);
            reservationRepository.save(conflictReservation);
        }

        reservation.setReason("ACCEPTED");
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }



    public Boolean reservationsNotEnded(Long accommodationId) {
        List<Reservation> reservationsNotEnded = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);
        return !reservationsNotEnded.isEmpty();
    }

    public Boolean reservationsNotEnded(Long accommodationId, Long availabilityId) {
        List<Reservation> reservationsNotEnded = reservationRepository
                .findReservationNotEndedByAvailabilityIdAndAccommodationId(accommodationId, availabilityId);
        return !reservationsNotEnded.isEmpty();
    }

    public void deletePendingReservation(Long accommodationId, Long reservationId, Long guestId) {


        var reservationWrapper = reservationRepository.findById(reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");

        reservationWrapper = reservationRepository.findByIdAndGuest(reservationId, guestId);
        if(reservationWrapper.isEmpty()) throw new InvalidAuthorizationException("You do not own this reservation");
        var reservation = reservationWrapper.get();

        if(!reservation.getStatus().equals(ReservationStatus.PENDING)) throw new InvalidAuthorizationException("You cannot delete non pending reservation");
        reservationRepository.delete(reservation);
    }

    public Page<ReservationDTO> getReservations(String addressOrName, LocalDate minDate, LocalDate maxDate,
                                                ReservationStatus reservationStatus, int pageNo, int pageSize,
                                                Long userId) {


        List<Reservation> reservations;

        if(ownerRepository.findOwnerById(userId).isPresent()) reservations = reservationRepository.findHostReservations(minDate, maxDate, reservationStatus, addressOrName, userId);
        else if(guestRepository.findGuestById(userId).isPresent()) reservations = reservationRepository.findGuestReservations(minDate, maxDate, reservationStatus, addressOrName, userId);
        else throw new NonExistingEntityException("User with this id doesn't exist");

        List<ReservationDTO> reservationsDTO = new ArrayList<>(reservations.stream().map(a -> {
            ReservationDTO r = new ReservationDTO();
            r.setReservationId(a.getId());
            r.setPrice(a.getPrice());
            r.setStatus(a.getStatus());
            r.setReason(a.getReason());
            r.setReservationEndDate(a.getReservationEndDate());
            r.setReservationStartDate(a.getReservationStartDate());
            r.setCancelDeadline(a.getCancelDeadline());
            r.setAccommodationName(a.getAccommodation().getName());
            r.setAccommodationId(a.getAccommodation().getId());
            r.setUserEmail(a.getGuest().getEmail());
            return r;
        }).toList());

        return PageConverter.convertListToPage(pageNo, pageSize, reservationsDTO);
    }

    public Page<ReservationDTO> getConflictReservations(Long accommodationId, Long reservationId, User user, int pageNo, int pageSize) {

        if(user instanceof Owner){
            Optional<Owner> ownerWrapper = ownerRepository.findOwnerById(user.getId());
            Owner owner = ownerWrapper.get();
            Optional<Accommodation> accommodationWrapper = accommodationRepository.findByIdAndOwnerId(accommodationId, owner.getId());
            if(accommodationWrapper.isEmpty()) throw new InvalidAuthorizationException("You do not own this accommodation");
        }


        Optional<Reservation> reservationWrapper = reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("This reservation does not exist");
        var reservation = reservationWrapper.get();
//        if(!reservation.getStatus().equals(ReservationStatus.PENDING)) throw new InvalidAuthorizationException("Only pending reservations can be selected");

        List<Reservation> conflictReservations = reservationRepository.findPendingConflictedReservations(accommodationId, reservationId);
        List<ReservationDTO> reservationsDTO = new ArrayList<>(conflictReservations.stream().map(a -> {
            ReservationDTO r = new ReservationDTO();
            r.setReservationId(a.getId());
            r.setPrice(a.getPrice());
            r.setStatus(a.getStatus());
            r.setReason(a.getReason());
            r.setReservationEndDate(a.getReservationEndDate());
            r.setReservationStartDate(a.getReservationStartDate());
            r.setCancelDeadline(a.getCancelDeadline());
            r.setAccommodationName(a.getAccommodation().getName());
            r.setAccommodationId(a.getAccommodation().getId());
            r.setUserEmail(a.getGuest().getEmail());
            return r;
        }).toList());

        return PageConverter.convertListToPage(pageNo, pageSize, reservationsDTO);
    }

    public ReservationDTO getReservationDetails(Long accommodationId, Long reservationId, Long userId) {


        Reservation reservation = null;
        var reservationWrapper = reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");
        reservation = reservationWrapper.get();

        Optional<AccommodationReview> accommodationReviewWrapper = Optional.empty();
        Optional<OwnerReview> ownerReviewWrapper = Optional.empty();


        if(ownerRepository.findOwnerById(userId).isPresent()) {
            if(accommodationRepository.findByIdAndOwnerId(accommodationId, userId).isEmpty()) throw new InvalidAuthorizationException("You do not own this accommodation");

            Guest guest = reservation.getGuest();

            accommodationReviewWrapper = accommodationReviewRepository.findByAccommodationIdAndGuestId(accommodationId, guest.getId());
            ownerReviewWrapper = ownerReviewRepository.findByOwnerIdAndGuestId(userId, guest.getId());

        }
        else if(guestRepository.findGuestById(userId).isPresent()) {

            if(reservationRepository.findByIdAndGuest(reservationId, userId).isEmpty()) throw new InvalidAuthorizationException("You do not own this reservation");

            Owner owner = reservation.getAccommodation().getOwner();

            accommodationReviewWrapper = accommodationReviewRepository.findByAccommodationIdAndGuestId(accommodationId, userId);
            ownerReviewWrapper = ownerReviewRepository.findByOwnerIdAndGuestId(owner.getId(), userId);

        }else if(adminRepository.findAdminById(userId).isPresent()) {


            Guest guest = reservation.getGuest();
            Owner owner = reservation.getAccommodation().getOwner();

            accommodationReviewWrapper = accommodationReviewRepository.findByAccommodationIdAndGuestId(accommodationId, guest.getId());
            ownerReviewWrapper = ownerReviewRepository.findByOwnerIdAndGuestId(owner.getId(), guest.getId());

        }
        else throw new NonExistingEntityException("User with this id doesn't exist");

        List<Reservation> canceledReservations = reservationRepository.findGuestCanceledReservations(reservationId, userId);
        List<Reservation> conflictReservations = reservationRepository.findPendingConflictedReservations(accommodationId, reservationId);




        Owner owner = reservation.getAccommodation().getOwner();

        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setReservationId(reservation.getId());
        reservationDTO.setOwnerId(owner.getId());
        reservationDTO.setOwnerNameAndSurname(owner.getFirstName() + " " + owner.getLastname());
        reservationDTO.setOwnerEmail(owner.getEmail());
        reservationDTO.setAccommodationId(reservation.getAccommodation().getId());
        reservationDTO.setAccommodationName(reservation.getAccommodation().getName());
        reservationDTO.setAccommodationAddress(reservation.getAccommodation().getAddress());
        reservationDTO.setReservationStartDate(reservation.getReservationStartDate());
        reservationDTO.setReservationEndDate(reservation.getReservationEndDate());
        reservationDTO.setCancelDeadline(reservation.getCancelDeadline());
        reservationDTO.setGuestNum(reservation.getGuestNum());
        reservationDTO.setUnitPrice(reservation.getUnitPrice());
        reservationDTO.setPerGuest(reservation.getPerGuest());
        reservationDTO.setPrice(reservation.getPrice());
        reservationDTO.setStatus(reservation.getStatus());
        reservationDTO.setConflictReservations(!conflictReservations.isEmpty());
        reservationDTO.setReason(reservation.getReason());
        reservationDTO.setGuestId(reservation.getGuest().getId());
        reservationDTO.setReviewPresent(accommodationReviewWrapper.isPresent() || ownerReviewWrapper.isPresent());
        reservationDTO.setNameAndSurname(reservation.getGuest().getFirstName() + " " + reservation.getGuest().getLastname());
        reservationDTO.setUserEmail(reservation.getGuest().getEmail());
        reservationDTO.setTimesUserCancel((long) canceledReservations.size());
        
        return reservationDTO;

    }

    public Map<String, MonthlyReportDTO> getLast12MonthsReport(Long accommodationId) {
        System.out.println("POGODIO SERVIS POZIV");
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(12).withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        // Pronaći sve prihvaćene rezervacije za određeni smeštaj u poslednjih 12 meseci
        List<Reservation> reservations = reservationRepository.findByAccommodationIdAndDateRangeAndStatus(accommodationId, ReservationStatus.ACCEPTED, startDate, endDate);
        System.out.println("PRONADJENE REZ");
        System.out.println(reservations);

        // Inicijalizovati mapu za prikupljanje ukupnog profita i broja rezervacija po mesecima
        Map<String, MonthlyReportDTO> monthlyData = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = now.minusMonths(12 - month).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
            String monthYear = monthStart.getMonth().toString() + " " + monthStart.getYear();

            MonthlyReportDTO report = new MonthlyReportDTO();
            report.setMonthYear(monthYear);
            report.setTotalProfit(0.0);
            report.setReservationCount(0);
            monthlyData.put(monthYear, report);
        }

        // Izračunati ukupni profit i broj rezervacija po mesecima
        for (Reservation reservation : reservations) {
            LocalDate startDateReservation = reservation.getReservationStartDate();
            LocalDate endDateReservation = reservation.getReservationEndDate();

            LocalDate currentMonthStart = startDate.withDayOfMonth(1);
            LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());

            if (startDateReservation.isBefore(currentMonthEnd) && endDateReservation.isAfter(currentMonthStart)) {
                String monthYear = startDateReservation.getMonth().toString() + " " + startDateReservation.getYear();
                MonthlyReportDTO report = monthlyData.get(monthYear);

                double totalPrice = reservation.getPrice();
                report.setTotalProfit(report.getTotalProfit() + totalPrice);
                report.setReservationCount(report.getReservationCount() + 1);
                monthlyData.put(monthYear, report);
            }
        }

        return monthlyData;
    }



}
