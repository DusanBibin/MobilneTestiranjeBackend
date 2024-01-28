package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityRequestService {

    private final AvailabilityService availabilityService;
    private final AccommodationRepository accommodationRepository;
    private final AvailabilityRequestRepository availabilityRequestRepository;
    private final AvailabilityRepository availabilityRepository;
    public void createAvailabilityRequest(AvailabilityDTO availDTO, Long accommodationId, Owner owner) {

        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!accommodation.getOwner().getEmail().equals(owner.getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        if(availabilityService.availabilityRangeTaken(accommodationId, availDTO.getStartDate(), availDTO.getEndDate()))
                throw new InvalidDateException("There is already availability period that interferes with this period");


        var availabilityRequest = AvailabilityRequest.builder()
                .pricePerGuest(availDTO.getPricePerGuest())
                .price(availDTO.getPrice())
                .cancelDeadline(availDTO.getCancellationDeadline())
                .startDate(availDTO.getStartDate())
                .endDate(availDTO.getEndDate())
                .accommodation(accommodation)
                .availability(null)
                .status(RequestStatus.PENDING)
                .requestType(RequestType.CREATE)
                .reason("")
                .build();

        availabilityRequestRepository.save(availabilityRequest);


    }

    public void createEditAvailabilityRequest(AvailabilityDTO availDTO, Long accommodationId, Owner owner, Long availId) {

        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!accommodation.getOwner().getEmail().equals(owner.getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        var availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(availId, accommodationId);
        if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("The availability with this id does not exist");
        var availability = availabilityWrapper.get();


        if(availabilityService.availabilityRangeTaken(accommodationId, availDTO.getStartDate(), availDTO.getEndDate(), availId))
            throw new InvalidDateException("There is already availability period that interferes with this period");


        if(availabilityService.reservationsNotEnded(accommodationId, availId))
            throw new ReservationNotEndedException("You cannot change details if there are active reservations for this period");

        var availabilityRequest = AvailabilityRequest.builder()
                .pricePerGuest(availDTO.getPricePerGuest())
                .price(availDTO.getPrice())
                .cancelDeadline(availDTO.getCancellationDeadline())
                .startDate(availDTO.getStartDate())
                .endDate(availDTO.getEndDate())
                .accommodation(accommodation)
                .availability(availability)
                .status(RequestStatus.PENDING)
                .requestType(RequestType.EDIT)
                .reason("")
                .build();

        availabilityRequestRepository.save(availabilityRequest);
    }

    public void createDeleteAvailabilityRequest(Long accommodationId, Owner owner, Long availabilityId) {
        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!accommodation.getOwner().getEmail().equals(owner.getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        var availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(availabilityId, accommodationId);
        if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("The availability with this id does not exist");
        var availability = availabilityWrapper.get();


        if(availabilityService.reservationsNotEnded(accommodationId, availabilityId))
            throw new ReservationNotEndedException("You cannot change details if there are active reservations for this period");


        var availabilityRequest = AvailabilityRequest.builder()
                .pricePerGuest(availability.getPricePerGuest())
                .price(availability.getPrice())
                .cancelDeadline(availability.getCancelDeadline())
                .startDate(availability.getStartDate())
                .endDate(availability.getEndDate())
                .accommodation(accommodation)
                .availability(availability)
                .status(RequestStatus.PENDING)
                .requestType(RequestType.DELETE)
                .reason("")
                .build();

        availabilityRequestRepository.save(availabilityRequest);

    }

    public void acceptRequest(Long requestId) {
        var requestWrapper = availabilityRequestRepository.findById(requestId);
        if(requestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        AvailabilityRequest request = requestWrapper.get();
        Accommodation accommodation = request.getAccommodation();


        if(!request.getStatus().equals(RequestStatus.PENDING)) throw new InvalidEnumValueException("You can only accept a pending request");
        request.setStatus(RequestStatus.ACCEPTED);
        request.setReason("ACCEPTED");



        if(request.getRequestType().equals(RequestType.CREATE)){
            var availability = Availability.builder()
                    .accommodation(accommodation)
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .cancelDeadline(request.getCancelDeadline())
                    .price(request.getPrice())
                    .pricePerGuest(request.getPricePerGuest())
                    .build();

            availabilityRepository.save(availability);
            accommodation.getAvailabilityList().add(availability);
        }

        if(request.getRequestType().equals(RequestType.EDIT)){
            var availability = request.getAvailability();
            availability.setStartDate(request.getStartDate());
            availability.setEndDate(request.getEndDate());
            availability.setCancelDeadline(request.getCancelDeadline());
            availability.setPrice(request.getPrice());
            availability.setPricePerGuest(request.getPricePerGuest());

            availabilityRepository.save(availability);
        }


        if(request.getRequestType().equals(RequestType.DELETE)){
            var availability = request.getAvailability();
            request.setAvailability(null);
            availabilityRequestRepository.save(request);
            availabilityRepository.delete(availability);
        }

    }

    public void declineRequest(String reason, Long requestId) {
        var requestWrapper = availabilityRequestRepository.findById(requestId);
        if(requestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        AvailabilityRequest request = requestWrapper.get();


        if(!request.getStatus().equals(RequestStatus.PENDING)) throw new InvalidEnumValueException("You can only reject a pending request");
        request.setStatus(RequestStatus.REJECTED);
        request.setReason(reason);

        availabilityRequestRepository.save(request);
    }
}
