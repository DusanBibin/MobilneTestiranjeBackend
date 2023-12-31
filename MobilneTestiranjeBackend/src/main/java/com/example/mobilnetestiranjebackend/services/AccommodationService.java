package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.AccommodationAlreadyExistsException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AvailabilityRepository availabilityRepository;
    private final AccommodationRepository accommodationRepository;
    private final OwnerRepository ownerRepository;


    private static final String UPLOAD_DIR = "uploads";
    public void createAccommodation(Integer ownerId, List<MultipartFile> images, AccommodationDTO accommodationDTO) throws IOException {

        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        Owner owner = ownerWrapper.orElseThrow();

        var accommodationWrapper = accommodationRepository.findAccommodationsByOwnerAndName(owner, accommodationDTO.getName());
        if(accommodationWrapper.isPresent()) throw new AccommodationAlreadyExistsException("You already have accommodation with this name");


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
            if(accAvail.getCancellationDeadline().isBefore(accAvail.getEndDate()))
                throw new InvalidDateException("Cancellation date cannot be before end date");
        }



        List<String> imagePaths = new ArrayList<>();
        for(MultipartFile image: images){
            String relativePath = saveFile(owner.getEmail(), accommodationDTO.getName(), image);
            imagePaths.add(relativePath);
        }

        var accommodation = Accommodation.builder()
                .name(accommodationDTO.getName())
                .description(accommodationDTO.getDescription())
                .address(accommodationDTO.getAddress())
                .lat(accommodationDTO.getLat())
                .lon(accommodationDTO.getLon())
                .amenities(amenities)
                .minGuests(accommodationDTO.getMinGuests())
                .maxGuests(accommodationDTO.getMaxGuests())
                .accommodationType(AccommodationType.valueOf(accommodationDTO.getAccommodationType()))
                .approved(false)
                .autoAcceptEnabled(false)
                .photos(imagePaths)
                .availabilityList(new ArrayList<>())
                .build();


        accommodationRepository.save(accommodation);


        for(AccommodationAvailabilityDTO availabilityDTO: accommodationDTO.getAvailabilityList()){
            var accAvailability = AccommodationAvailability.builder()
                    .price(availabilityDTO.getPrice())
                    .endDate(availabilityDTO.getEndDate())
                    .startDate(availabilityDTO.getStartDate())
                    .accommodation(accommodation)
                    .build();

            availabilityRepository.save(accAvailability);
            accommodation.getAvailabilityList().add(accAvailability);
        }

        accommodation.setOwner(owner);
        accommodationRepository.save(accommodation);

        owner.getAccommodations().add(accommodation);
        ownerRepository.save(owner);


    }

    public String saveFile(String email, String accommodationName, MultipartFile file) throws IOException {
        String relativePath = "/" + email + "/" + accommodationName;
        Path uploadPath = Paths.get(UPLOAD_DIR + relativePath).toAbsolutePath().normalize();
        File uploadDir = uploadPath.toFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = Paths.get(uploadPath.toString(), fileName);

        file.transferTo(filePath.toFile());

        return relativePath;
    }


}

