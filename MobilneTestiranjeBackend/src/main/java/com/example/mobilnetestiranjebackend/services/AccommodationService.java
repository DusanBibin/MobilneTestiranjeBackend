package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.*;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
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
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final GuestRepository guestRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final OwnerRepository ownerRepository;
    private final ReviewService reviewService;

    public Optional<Accommodation> findAccommodationById(Long accommodationId) {
        return accommodationRepository.findAccommodationById(accommodationId);
    }


    public void addToFavorites(Long accommodationId, Long guestId) {

        var guestWrapper = guestRepository.findGuestById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        var accommodation = accommodationWrapper.get();


        var favoriteAccommodationWrapper = accommodationRepository.findFavoritesByAccommodationIdAndGuestId(accommodationId, guestId);
        if(favoriteAccommodationWrapper.isPresent()) throw new InvalidAuthorizationException("You already have this accommodation in favorites");


        guest.getFavorites().add(accommodation);
        guestRepository.save(guest);

    }

    public void removeFromFavorites(Long accommodationId, Long guestId) {

        var guestWrapper = guestRepository.findGuestById(guestId);
        if(guestWrapper.isEmpty()) throw new NonExistingEntityException("Guest with this id doesn't exist");
        var guest = guestWrapper.get();

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");

        var favoriteAccommodationWrapper = accommodationRepository.findFavoritesByAccommodationIdAndGuestId(accommodationId, guestId);
        if(favoriteAccommodationWrapper.isEmpty()) throw new InvalidAuthorizationException("This id doesn't exist in your favorites");
        var favoriteAccommodation = favoriteAccommodationWrapper.get();



        guest.getFavorites().remove(favoriteAccommodation);
        guestRepository.save(guest);


    }

    public Page<AccommodationSearchDTO> searchAccommodations(Long guestNum, String address, LocalDate startDate, LocalDate endDate,
                                     List<Amenity> amenities, AccommodationType accommodationType, Long minPrice,
                                     Long maxPrice, int pageNo, int pageSize, Boolean isAscending, String sortType) {
        Long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        //RADIMO QUERY ZA ULAZNE PARAMETRE
        List<Accommodation> temp1 = accommodationRepository.searchAccommodations(guestNum, address,
                startDate, endDate, accommodationType, minPrice, maxPrice, daysBetween);


        List<Accommodation> temp2 = new ArrayList<>();

        //PROVERA AMENITIES DA LI SU SVI TU
        if(amenities == null || amenities.isEmpty()) temp2 = temp1;
        else{
            Set<Amenity> tempAmenities = new HashSet<>(amenities);
            for(Accommodation a: temp1){
                var tempSet = new HashSet<>(a.getAmenities());
                if(tempSet.equals(tempAmenities)) temp2.add(a);
            }
        }
        List<Accommodation> foundAccommodations = new ArrayList<>();


        Map<Long, Availability> map = new HashMap<Long, Availability>();
        Map<Long, LocalDate> mapStartDates = new HashMap<>();
        Map<Long, LocalDate> mapEndDates = new HashMap<>();
        //PROVERA REZERVACIJA DA LI SE NEKE POKLAPAJU
        for(Accommodation a: temp2){


            List<Availability> availabilities = availabilityRepository.findAllByAccommodationId(a.getId());

            for(Availability av: availabilities){
                boolean available = true;

                LocalDate pomStartDate = startDate;
                LocalDate pomEndDate = endDate;

                if(startDate.compareTo(av.getStartDate()) >=0 && startDate.compareTo(av.getEndDate()) <=0 && endDate.compareTo(av.getEndDate()) > 0){
                    pomEndDate = av.getEndDate();

                }else if(endDate.compareTo(av.getStartDate()) >=0 && endDate.compareTo(av.getEndDate()) <=0 && startDate.compareTo(av.getStartDate()) < 0){
                    pomStartDate = av.getStartDate();

                }else if(startDate.compareTo(av.getStartDate()) < 0 && endDate.compareTo(av.getEndDate()) > 0){
                    pomStartDate = av.getStartDate();
                    pomEndDate = av.getEndDate();

                }else if(startDate.compareTo(av.getStartDate()) >= 0 && endDate.compareTo(av.getEndDate()) <=0){

                }else continue;

                List<Reservation> conflictedReservations = reservationRepository.findAcceptedReservationsInConflict(pomStartDate, pomEndDate, a.getId(), av.getId());
                for(Reservation r: conflictedReservations){
                    if(pomStartDate.compareTo(r.getReservationStartDate()) >=0 && pomStartDate.compareTo(r.getReservationEndDate()) <=0 && pomEndDate.compareTo(r.getReservationEndDate()) > 0){
                        pomStartDate = r.getReservationEndDate().plusDays(1);
                    }else if(pomEndDate.compareTo(r.getReservationStartDate()) >=0 && pomEndDate.compareTo(r.getReservationEndDate()) <=0 && pomStartDate.compareTo(r.getReservationStartDate()) < 0){
                        pomEndDate = r.getReservationStartDate().minusDays(1);
                    }else if(pomStartDate.compareTo(r.getReservationStartDate()) < 0 && pomEndDate.compareTo(r.getReservationEndDate()) > 0){
                        pomEndDate = r.getReservationStartDate().minusDays(1);
                    }else{
                        available = false;
                        break;
                    }
                }

                if(available){
                    mapStartDates.put(a.getId(), pomStartDate);
                    mapEndDates.put(a.getId(), pomEndDate);
                    map.put(a.getId(), av);
                    foundAccommodations.add(a);
                    break;
                }

            }
        }

        List<AccommodationSearchDTO> convertedList = new ArrayList<>(foundAccommodations.stream().map(a -> {
            Availability av = map.get(a.getId());
            LocalDate startCorrected = mapStartDates.get(a.getId());
            LocalDate endCorrected = mapEndDates.get(a.getId());

            Long totalPrice = 0L;


            Long days = ChronoUnit.DAYS.between(startCorrected, endCorrected) + 1;


            if (av.getPricePerGuest()) totalPrice = days * av.getPrice() * guestNum;
            else totalPrice = days * av.getPrice();

            Double ratingAvg = reviewService.getAverageAccommodationRating(a.getId());

            AccommodationSearchDTO dto = new AccommodationSearchDTO();
            dto.setAccommodationId(a.getId());
            dto.setName(a.getName());
            dto.setAddress(a.getAddress());
            dto.setAmenities(a.getAmenities());
            dto.setTotalPrice(totalPrice);
            dto.setOneNightPrice(av.getPrice());
            dto.setIsPerPerson(av.getPricePerGuest());
            dto.setMinGuests(a.getMinGuests());
            dto.setMaxGuests(a.getMaxGuests());
            dto.setAccommodationType(a.getAccommodationType());
            dto.setRating(ratingAvg);
            dto.setDateStart(startCorrected);
            dto.setDateEnd(endCorrected);

            return dto;
        }).toList());
        //sortiranje

        if(sortType.toLowerCase().equals("price")){
            if(isAscending) convertedList.sort(Comparator.comparingLong(AccommodationSearchDTO::getTotalPrice));
            else convertedList.sort(Comparator.comparingLong(AccommodationSearchDTO::getTotalPrice).reversed());
        }else{
            if(isAscending) convertedList.sort(Comparator.comparing(AccommodationSearchDTO::getName));
            else convertedList.sort(Comparator.comparing(AccommodationSearchDTO::getName).reversed());
        }

        //Page<AccommodationSearchDTO> dtoPage = convertListToPage(pageNo, pageSize, convertedList);
        Page<AccommodationSearchDTO> dtoPage = PageConverter.convertListToPage(pageNo, pageSize, convertedList);
        return dtoPage;
    }

    public Page<AccommodationViewDTO> getOwnerAccommodations(Long ownerId, int pageNo, int pageSize) {
        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        if(ownerWrapper.isEmpty()) throw new InvalidInputException("This user doesn't exist");


        List<Accommodation> accommodations = accommodationRepository.findByOwnerId(ownerId);
        List<AccommodationViewDTO> convertedList = new ArrayList<>(accommodations.stream().map(a -> {
            AccommodationViewDTO accommodation = new AccommodationViewDTO();
            accommodation.setId(a.getId());
            accommodation.setName(a.getName());
            accommodation.setAddress(a.getAddress());
            return accommodation;
        }).toList());

        return PageConverter.convertListToPage(pageNo, pageSize, convertedList);
    }

    public Page<AccommodationViewDTO> getFavorites(Long guestId, int pageNo, int pageSize) {

        List<Accommodation> favorites = accommodationRepository.findFavoritesByGuestId(guestId);
        List<AccommodationViewDTO> convertedList = new ArrayList<>(favorites.stream().map(a -> {
            AccommodationViewDTO accommodation = new AccommodationViewDTO();
            accommodation.setId(a.getId());
            accommodation.setName(a.getName());
            accommodation.setAddress(a.getAddress());

            return accommodation;
        }).toList());

        return PageConverter.convertListToPage(pageNo, pageSize, convertedList);
    }

    public void toggleAutoAccept(Long accommodationId, Boolean status, Long ownerId) {
        if(ownerRepository.findOwnerById(ownerId).isEmpty()) throw new InvalidAuthorizationException("User with this id doesn't exist");


        Optional<Accommodation> accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new InvalidInputException("Accommodation with this id doesn't exist");

        accommodationWrapper = accommodationRepository.findByIdAndOwnerId(accommodationId, ownerId);
        if(accommodationWrapper.isEmpty()) throw new InvalidAuthorizationException("You do not own this accommodation");
        var accommodation = accommodationWrapper.get();

        if(status.equals(accommodation.getAutoAcceptEnabled())) throw new InvalidInputException("Auto accept is already " + (status ? "ON" : "OFF"));

        accommodation.setAutoAcceptEnabled(status);
        accommodationRepository.save(accommodation);
    }


    public AccommodationDTOResponse getAccommodation(Long accommodationId, User user) {

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();


        List<Long> imageIds = new ArrayList<>();
        for(String imagePath: accommodation.getImagePaths()){
            String[] pathParts = imagePath.split("/");
            String fileName = pathParts[3];

            if(!Character.isDigit(fileName.charAt(0))) throw new InvalidInputException("Error with image id");

            Long imageId = Long.parseLong(String.valueOf(fileName.charAt(0)));
            imageIds.add(imageId);
        }


        Boolean favorite = null;
        if(user instanceof Guest) {
            Optional<Accommodation> favoriteWrapper = accommodationRepository.findFavoritesByAccommodationIdAndGuestId(accommodationId, user.getId());
            favorite = favoriteWrapper.isPresent();
        }

        Owner owner = accommodation.getOwner();
        var accommodationDTO = AccommodationDTOResponse.builder()
                .ownerId(owner.getId())
                .ownerEmail(owner.getEmail())
                .ownerNameAndSurname(owner.getFirstName() + " " + owner.getLastname())
                .id(accommodation.getId())
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .address(accommodation.getAddress())
                .lat(accommodation.getLat())
                .lon(accommodation.getLon())
                .amenities(accommodation.getAmenities())
                .minGuests(accommodation.getMinGuests())
                .maxGuests(accommodation.getMaxGuests())
                .averageAccommodationRating(reviewService.getAverageAccommodationRating(accommodationId))
                .averageOwnerRating(reviewService.getAverageOwnerRating(accommodation.getOwner().getId()))
                .accommodationType(accommodation.getAccommodationType())
                .autoAcceptEnabled(accommodation.getAutoAcceptEnabled())
                .availabilityList(new ArrayList<>())
                .futureReservations(new ArrayList<>())
                .favorite(favorite)
                .imageIds(imageIds)
                .build();


        for(Availability a: accommodation.getAvailabilityList()){
            if(a.getStartDate().isAfter(LocalDate.now()) && a.getEndDate().isAfter(LocalDate.now())){
                var availabilityDTO = AvailabilityDTO.builder()
                        .id(a.getId())
                        .startDate(a.getStartDate())
                        .endDate(a.getEndDate())
                        .cancellationDeadline(a.getCancelDeadline())
                        .pricePerGuest(a.getPricePerGuest())
                        .price(a.getPrice())
                        .build();
                accommodationDTO.getAvailabilityList().add(availabilityDTO);
            }
        }

        var futureReservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);

        for(Reservation r : futureReservations){
            var reservationDTO = ReservationDTO.builder()
                    .availabilityId(r.getAvailability().getId())
                    .reservationEndDate(r.getReservationEndDate())
                    .reservationStartDate(r.getReservationStartDate())
                    .guestNum(r.getGuestNum())
                    .build();
            accommodationDTO.getFutureReservations().add(reservationDTO);
        }

        return accommodationDTO;
    }
}

