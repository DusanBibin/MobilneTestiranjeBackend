package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final AvailabilityRequestRepository availabilityRequestRepository;
    private final OwnerRepository ownerRepository;
    private final AvailabilityService availabilityService;


    private static final String UPLOAD_DIR = "uploads";


    private List<Amenity> checkInputValues(AccommodationDTO accommodationDTO){

        try {
            AccommodationType.valueOf(accommodationDTO.getAccommodationType());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Invalid accommodation value");
        }

        List<Amenity> amenities = new ArrayList<>();
        for(String amenityStr: accommodationDTO.getAmenities()){
            try {
                Amenity amenity = Amenity.valueOf(amenityStr);
                amenities.add(amenity);
            } catch (IllegalArgumentException e) {
                throw new InvalidEnumValueException("Invalid amenity value");
            }
        }

        for(AccommodationAvailabilityDTO accAvail: accommodationDTO.getAvailabilityList()){
            if(accAvail.getEndDate().isBefore(accAvail.getStartDate()))
                throw new InvalidDateException("End date cannot be before start date");
            if(accAvail.getCancellationDeadline().isAfter(accAvail.getStartDate()) ||
                    accAvail.getCancellationDeadline().isEqual(accAvail.getStartDate()))
                throw new InvalidDateException("Cancellation date cannot be after start date");
        }
        return amenities;
    }
    public void createAccommodationRequest(Integer ownerId, List<MultipartFile> images, AccommodationDTO accommodationDTO){

        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        Owner owner = ownerWrapper.orElseThrow();

        var accommodationWrapper = accommodationRepository.findAccommodationsByOwnerAndName(owner, accommodationDTO.getName());
        if(accommodationWrapper.isPresent()) throw new EntityAlreadyExistsException("You already have accommodation with this name");

        var amenities = checkInputValues(accommodationDTO);


        List<String> imagePaths = new ArrayList<>();

        if(images.size() > 5) throw new TooManyFilesException("You can only upload 5 images");

        for(MultipartFile image: images){

            checkExtension(image.getOriginalFilename());

            int currentIndex = images.indexOf(image);
            String relativePath = saveFile(owner.getEmail(), accommodationDTO.getName(), image, currentIndex + 1);
            imagePaths.add(relativePath);
        }

        var accommodationRequest = AccommodationRequest.builder()
                .name(accommodationDTO.getName())
                .description(accommodationDTO.getDescription())
                .address(accommodationDTO.getAddress())
                .lat(accommodationDTO.getLat())
                .lon(accommodationDTO.getLon())
                .amenities(amenities)
                .imagePaths(imagePaths)
                .minGuests(accommodationDTO.getMinGuests())
                .maxGuests(accommodationDTO.getMaxGuests())
                .accommodationType(AccommodationType.valueOf(accommodationDTO.getAccommodationType()))
                .autoAcceptEnabled(accommodationDTO.getAutoAcceptEnabled())
                .availabilityRequests(new ArrayList<AvailabilityRequest>())
                .owner(owner)
                .status(RequestStatus.PENDING)
                .build();

        accommodationRequestRepository.save(accommodationRequest);

        for(AccommodationAvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){
            var availabilityRequest = AvailabilityRequest.builder()
                    .startDate(availDTO.getStartDate())
                    .endDate(availDTO.getEndDate())
                    .cancelDeadline(availDTO.getCancellationDeadline())
                    .price(availDTO.getPrice())
                    .pricePerGuest(availDTO.getPricePerGuest())
                    .accommodationRequest(accommodationRequest)
                    .build();
            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);
    }



    public void createEditAccommodationRequest(Integer ownerId, List<MultipartFile> images, AccommodationDTO accommodationDTO, Long accommodationId) {
        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        Owner owner = ownerWrapper.orElseThrow();

        var accommodationWrapper = findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");

        var amenities = checkInputValues(accommodationDTO);

        if(availabilityService.reservationsNotEnded(accommodationId))
            throw new ReservationNotEndedException("One or more reservations for this availability period haven't ended yet");


        List<String> imagePaths = new ArrayList<>();
        for(MultipartFile image: images){
            int currentIndex = images.indexOf(image);
            String relativePath = saveFile(owner.getEmail(), accommodationDTO.getName(), image, currentIndex);
            imagePaths.add(relativePath);
        }
    }

    public Optional<Accommodation> findAccommodationById(Long accommodationId) {
        return accommodationRepository.findAccommodationById(accommodationId);
    }

    public String saveFile(String email, String accommodationName, MultipartFile file, Integer currentIndex){
        String relativePath = "/" + email + "/" + accommodationName;
        Path uploadPath = Paths.get(UPLOAD_DIR + relativePath).toAbsolutePath().normalize();
        File uploadDir = uploadPath.toFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = currentIndex + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadPath.toString(), fileName);
        System.out.println(filePath.getFileName());
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return relativePath + "/" + filePath.getFileName();
    }

    private void checkExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new InvalidFileExtensionException("The file doesn't have extension");
        }

        String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();

        if(!fileExtension.equals("png") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg"))
            throw new InvalidFileExtensionException("You can only upload .jpg or .png");
    }

}

