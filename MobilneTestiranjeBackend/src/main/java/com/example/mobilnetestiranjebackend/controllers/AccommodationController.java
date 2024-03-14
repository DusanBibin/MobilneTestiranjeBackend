package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTOResponse;
import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidFileExtensionException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;



    @GetMapping(path = "/{accommodationId}")
    public ResponseEntity<?> getAccommodation(@PathVariable("accommodationId") Long accommodationId){

        var accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        var accommodationDTO = AccommodationDTOResponse.builder()
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .address(accommodation.getAddress())
                .lat(accommodation.getLat())
                .lon(accommodation.getLon())
                .amenities(accommodation.getAmenities())
                .minGuests(accommodation.getMinGuests())
                .maxGuests(accommodation.getMaxGuests())
                .accommodationType(accommodation.getAccommodationType())
                .autoAcceptEnabled(accommodation.getAutoAcceptEnabled())
                .availabilityList(new ArrayList<>())
                .imagePaths(accommodation.getImagePaths())
                .build();


        for(Availability a: accommodation.getAvailabilityList()){
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


        return ResponseEntity.ok().body(accommodationDTO);
    }


    @GetMapping(value = "/{accommodationId}/images/{imageId}")
    public @ResponseBody ResponseEntity<?> getImageWithMediaType(@PathVariable("accommodationId") Long accommodationId,
                                                      @PathVariable("imageId") Long imageId) throws IOException {

        var accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();


        String foundImgPath = "";
        MediaType foundImgType = null;

        for(String imgPath: accommodation.getImagePaths()){
            String[] pathParts = imgPath.split("/");
            String fileName = pathParts[3];

            if(fileName.charAt(0) == String.valueOf(imageId).charAt(0)){
                foundImgPath = imgPath;

                int dotIndex = fileName.lastIndexOf('.');

                if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
                    throw new InvalidFileExtensionException("The file doesn't have extension");
                }

                String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
                if(fileExtension.equals("png")) foundImgType = MediaType.IMAGE_JPEG;
                if(fileExtension.equals("jpg") || fileExtension.equals("jpeg")) foundImgType = MediaType.IMAGE_PNG;

                break;
            }
        }

        if(foundImgPath.isEmpty()) throw new NonExistingEntityException("The image with this path does not exist");


        Path filePath = Path.of("uploads/" + foundImgPath);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found");
        }

        byte[] imageBytes = Files.readAllBytes(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(foundImgType);
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, 200);
    }


    @PreAuthorize("hasAuthority('GUEST')")
    @PutMapping(path = "/{accommodationId}/add-favorites")
    public ResponseEntity<?> addToFavorites(@PathVariable("accommodationId") Long accommodationId,
                                            @AuthenticationPrincipal Guest guest){


        accommodationService.addToFavorites(accommodationId, guest.getId());

        return new ResponseEntity<>(("Successfully added accommodation to favorites"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('GUEST')")
    @PutMapping(path = "/{accommodationId}/remove-favorites")
    public ResponseEntity<?> removeFromFavorites(@PathVariable("accommodationId") Long accommodationId,
                                            @AuthenticationPrincipal Guest guest){


        accommodationService.removeFromFavorites(accommodationId, guest.getId());

        return new ResponseEntity<>(("Successfully removed accommodation from favorites"), HttpStatus.OK);
    }



}
