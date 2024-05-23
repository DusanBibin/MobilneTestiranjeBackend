package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTOResponse;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.exceptions.InvalidFileExtensionException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("UZIMAMO SLIKU");
        return new ResponseEntity<>(imageBytes, headers, 200);
    }


    @GetMapping("")
    public ResponseEntity<?> getAccommodationsSearch(@RequestParam(defaultValue = "1") Long guestNum, @RequestParam @NotBlank(message = "Address must be present") String address,
                                                     @RequestParam @FutureOrPresent(message = "Start date must be in the future") LocalDate startDate,
                                                     @RequestParam @FutureOrPresent(message = "End date must be in the future") LocalDate endDate,
                                                     @RequestParam(required = false) List<Amenity> amenities,
                                                     @RequestParam(required = false) AccommodationType accommodationType,
                                                     @RequestParam(required = false) Long minPrice,
                                                     @RequestParam(required = false) Long maxPrice,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize){


        if(startDate.isBefore(LocalDate.now())) throw new InvalidInputException("Start date must be in the future");
        if(endDate.isBefore(LocalDate.now())) throw new InvalidInputException("End date must be in the future");
        if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) throw new InvalidInputException("Start date must be before end date");

        Page<AccommodationSearchDTO> pagedAccommodations = accommodationService.searchAccommodations(guestNum, address, startDate, endDate, amenities, accommodationType,
                minPrice, maxPrice, pageNo, pageSize);

        return ResponseEntity.ok().body(pagedAccommodations);
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
