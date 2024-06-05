package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final GuestRepository guestRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;

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
                                     Long maxPrice, int pageNo, int pageSize) {
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
        //PROVERA REZERVACIJA DA LI SE NEKE POKLAPAJU
        for(Accommodation a: temp2){

            boolean availabilityFree = false;
            List<Availability> availabilities = availabilityRepository.findAllByAccommodationId(a.getId());
            for(Availability av: availabilities){
                if(startDate.compareTo(av.getStartDate()) >= 0 && startDate.compareTo(av.getEndDate()) <= 0 && endDate.compareTo(av.getStartDate()) >=0 && endDate.compareTo(av.getEndDate()) <= 0){
                    List<Reservation> conflictedReservations = reservationRepository.findAcceptedReservationsInConflict(startDate, endDate, a.getId(), av.getId());
                    if(conflictedReservations.isEmpty()) {availabilityFree = true; map.put(a.getId(), av); break;}
                }
            }
            if(availabilityFree) foundAccommodations.add(a);
        }


        

        Page<Accommodation> pagedAccoms = convertListToPage(pageNo, pageSize, foundAccommodations);
        Page<AccommodationSearchDTO> dtoPage = pagedAccoms.map(new Function<Accommodation, AccommodationSearchDTO>() {
            @Override
            public AccommodationSearchDTO apply(Accommodation a) {

                Availability av = map.get(a.getId());

                Long totalPrice = 0L;

                if(av.getPricePerGuest()) totalPrice = daysBetween * av.getPrice() * guestNum;
                else totalPrice = daysBetween * av.getPrice();

                List<AccommodationReview> accommodationsRatings = accommodationReviewRepository.findByAccommodationId(a.getId());

                double ratingSum = 0;
                for(AccommodationReview ar: accommodationsRatings){
                    ratingSum += ar.getRating();
                }
                double ratingAvg = ratingSum / accommodationsRatings.size();

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

                return dto;
            }
        });

        return dtoPage;
    }

    private Page<Accommodation> convertListToPage(int page, int size, List<Accommodation> accommodationList){
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accommodationList.size());

        List<Accommodation> pageContent;
        if(start > end) pageContent = new ArrayList<>();
        else pageContent = accommodationList.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, accommodationList.size());
    }
}

