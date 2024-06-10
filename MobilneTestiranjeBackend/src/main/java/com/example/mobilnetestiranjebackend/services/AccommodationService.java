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
import java.util.stream.Collectors;

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
                                     Long maxPrice, int pageNo, int pageSize, Boolean isAscending, String sortType) {

        Long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        //RADIMO QUERY ZA ULAZNE PARAMETRE
        List<Accommodation> temp1 = accommodationRepository.searchAccommodations(guestNum, address,
                startDate, endDate, accommodationType, minPrice, maxPrice, daysBetween);


        System.out.println("VELICINA OVE LISTE JE: " + temp1.size());
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
            System.out.println("IMAMO OVOLIKO AVAILABILITIJEA: " + availabilities.size());
            for(Availability av: availabilities){
                boolean available = true;
                System.out.println("AV START: " + av.getStartDate());
                System.out.println("AV END: " + av.getEndDate());
                LocalDate pomStartDate = startDate;
                LocalDate pomEndDate = endDate;

                if(startDate.compareTo(av.getStartDate()) >=0 && startDate.compareTo(av.getEndDate()) <=0 && endDate.compareTo(av.getEndDate()) > 0){
                    pomEndDate = av.getEndDate();
                    System.out.println("USLI SMO OVDEE 1");
                }else if(endDate.compareTo(av.getStartDate()) >=0 && endDate.compareTo(av.getEndDate()) <=0 && startDate.compareTo(av.getStartDate()) < 0){
                    pomStartDate = av.getStartDate();
                    System.out.println("USLI SMO OVDEE 2");
                }else if(startDate.compareTo(av.getStartDate()) < 0 && endDate.compareTo(av.getEndDate()) > 0){
                    pomStartDate = av.getStartDate();
                    pomEndDate = av.getEndDate();
                    System.out.println("USLI SMO OVDEE 3");
                }else if(startDate.compareTo(av.getStartDate()) >= 0 && endDate.compareTo(av.getEndDate()) <=0){
                    System.out.println("USLI SMO OVDEE 4");
                }else continue;

                List<Reservation> conflictedReservations = reservationRepository.findAcceptedReservationsInConflict(pomStartDate, pomEndDate, a.getId(), av.getId());
                for(Reservation r: conflictedReservations){
                    if(pomStartDate.compareTo(r.getReservationStartDate()) >=0 && pomStartDate.compareTo(r.getReservationEndDate()) <=0 && pomEndDate.compareTo(r.getReservationEndDate()) > 0){
                        pomStartDate = r.getReservationEndDate().plusDays(1);
                        System.out.println("USLI SMO OVDEE 12");
                    }else if(pomEndDate.compareTo(r.getReservationStartDate()) >=0 && pomEndDate.compareTo(r.getReservationEndDate()) <=0 && pomStartDate.compareTo(r.getReservationStartDate()) < 0){
                        pomEndDate = r.getReservationStartDate().minusDays(1);
                        System.out.println("USLI SMO OVDEE 22");
                    }else if(pomStartDate.compareTo(r.getReservationStartDate()) < 0 && pomEndDate.compareTo(r.getReservationEndDate()) > 0){
                        pomEndDate = r.getReservationStartDate().minusDays(1);
                        System.out.println("USLI SMO OVDEE 32");
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

//                if( !(pomStartDate.isEqual(av.getEndDate()) || pomEndDate.isEqual(av.getStartDate())) ){
//                    mapStartDates.put(a.getId(), pomStartDate);
//                    mapEndDates.put(a.getId(), pomEndDate);
//                    map.put(a.getId(), av);
//                    availabilityFree = true;
//                    break;
//                }
            }
        }

        List<AccommodationSearchDTO> convertedList = new ArrayList<>(foundAccommodations.stream().map(a -> {
            Availability av = map.get(a.getId());
            LocalDate startCorrected = mapStartDates.get(a.getId());
            LocalDate endCorrected = mapEndDates.get(a.getId());
            System.out.println(startCorrected);
            System.out.println(endCorrected);
            Long totalPrice = 0L;



            Long days = ChronoUnit.DAYS.between(startCorrected, endCorrected) + 1;

            System.out.println("BROJ DANA JE : " + days);
//            Long days = daysBetween;
//            Long daysBetweenAvailability = ChronoUnit.DAYS.between(av.getStartDate(), av.getEndDate()) + 1;
//            if(daysBetween > daysBetweenAvailability) days = daysBetweenAvailability;


            if (av.getPricePerGuest()) totalPrice = days * av.getPrice() * guestNum;
            else totalPrice = days * av.getPrice();

            List<AccommodationReview> accommodationsRatings = accommodationReviewRepository.findByAccommodationId(a.getId());

            double ratingSum = 0;
            for (AccommodationReview ar : accommodationsRatings) {
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




        Page<AccommodationSearchDTO> dtoPage = convertListToPage(pageNo, pageSize, convertedList);

//        Page<Accommodation> pagedAccoms = convertListToPage(pageNo, pageSize, foundAccommodations);
//        Page<AccommodationSearchDTO> dtoPage = pagedAccoms.map(new Function<Accommodation, AccommodationSearchDTO>() {
//            @Override
//            public AccommodationSearchDTO apply(Accommodation a) {
//
//                Availability av = map.get(a.getId());
//
//                Long totalPrice = 0L;
//
//                if(av.getPricePerGuest()) totalPrice = daysBetween * av.getPrice() * guestNum;
//                else totalPrice = daysBetween * av.getPrice();
//
//                List<AccommodationReview> accommodationsRatings = accommodationReviewRepository.findByAccommodationId(a.getId());
//
//                double ratingSum = 0;
//                for(AccommodationReview ar: accommodationsRatings){
//                    ratingSum += ar.getRating();
//                }
//                double ratingAvg = ratingSum / accommodationsRatings.size();
//
//                AccommodationSearchDTO dto = new AccommodationSearchDTO();
//                dto.setAccommodationId(a.getId());
//                dto.setName(a.getName());
//                dto.setAddress(a.getAddress());
//                dto.setAmenities(a.getAmenities());
//                dto.setTotalPrice(totalPrice);
//                dto.setOneNightPrice(av.getPrice());
//                dto.setIsPerPerson(av.getPricePerGuest());
//                dto.setMinGuests(a.getMinGuests());
//                dto.setMaxGuests(a.getMaxGuests());
//                dto.setAccommodationType(a.getAccommodationType());
//                dto.setRating(ratingAvg);
//
//                return dto;
//            }
//        });

        return dtoPage;
    }

    private Page<AccommodationSearchDTO> convertListToPage(int page, int size, List<AccommodationSearchDTO> accommodationList){
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), accommodationList.size());

        List<AccommodationSearchDTO> pageContent;
        if(start > end) pageContent = new ArrayList<>();
        else pageContent = accommodationList.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, accommodationList.size());
    }
}

