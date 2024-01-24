package com.example.mobilnetestiranjebackend.service;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.repositories.*;
import com.example.mobilnetestiranjebackend.services.AccommodationRequestService;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AccommodationRequestServiceTest {

    @Mock
    private AccommodationRequestRepository accommodationRequestRepository;

    @Mock
    private AvailabilityRequestRepository availabilityRequestRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private AccommodationRequestService accommodationRequestService;

    private static final String UPLOAD_DIR = "uploads";


    @Test
    public void shouldCreateEditAccommodationRequest() {
        AccommodationAvailabilityDTO dto = new AccommodationAvailabilityDTO(1L, LocalDate.now().minusDays(20), LocalDate.now().plusDays(20),
                LocalDate.now().minusDays(30), true, 50L, RequestType.EDIT);
        List<AccommodationAvailabilityDTO> availabilityList = new ArrayList<>();
        availabilityList.add(dto);
        List<String> files = new ArrayList<>();
        files.add("putanja");

        List<Amenity> amenities = new ArrayList<>();
        amenities.add(Amenity.KITCHEN);
        amenities.add(Amenity.PARKING);

        AccommodationDTO accommodationDTO = new AccommodationDTO("name", "desc", "address", 15.0, 30.0, amenities,
                2L, 5L, AccommodationType.APARTMENT, true, availabilityList, files, RequestStatus.PENDING, "reason");
        Owner owner = new Owner();
        owner.setId(1L);
        when(ownerRepository.findOwnerById(1L)).thenReturn(Optional.of(owner));

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        when(accommodationRepository.findAccommodationById(1L)).thenReturn(Optional.of(accommodation));

        when(availabilityService.availabilityRangeTaken(accommodation.getId(),
                LocalDate.now().plusDays(20),
                LocalDate.now().minusDays(30),
                1L
                )).thenReturn(false);

        when(availabilityService.reservationsNotEnded(accommodation.getId())).thenReturn(true);

        var accommodationRequest = AccommodationRequest.builder()
                .name(accommodationDTO.getName())
                .description(accommodationDTO.getDescription())
                .address(accommodationDTO.getAddress())
                .lat(accommodationDTO.getLat())
                .lon(accommodationDTO.getLon())
                .amenities(amenities)
                .imagePaths(files)
                .minGuests(accommodationDTO.getMinGuests())
                .maxGuests(accommodationDTO.getMaxGuests())
                .accommodationType(accommodationDTO.getAccommodationType())
                .autoAcceptEnabled(accommodationDTO.getAutoAcceptEnabled())
                .availabilityRequests(new ArrayList<AvailabilityRequest>())
                .owner(owner)
                .accommodation(accommodation)
                .status(RequestStatus.PENDING)
                .id(1L)
                .build();

        when(accommodationRequestRepository.save(accommodationRequest)).thenReturn(accommodationRequest);
    }
}
