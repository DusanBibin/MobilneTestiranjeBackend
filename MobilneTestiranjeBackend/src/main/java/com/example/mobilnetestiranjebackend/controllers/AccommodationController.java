package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.*;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidFileExtensionException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import com.example.mobilnetestiranjebackend.services.AccommodationRequestService;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final ReservationRepository reservationRepository;
    private final AccommodationRequestService accommodationRequestService;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final AccommodationRepository accommodationRepository;


    @GetMapping(path = "/{accommodationId}")
    public ResponseEntity<?> getAccommodation(@PathVariable("accommodationId") Long accommodationId,
                                              @AuthenticationPrincipal User user){

        AccommodationDTOResponse accommodationDTO = accommodationService.getAccommodation(accommodationId, user);

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
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(Path.of(foundImgPath).getFileName().toString())
                .build());
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
                                                     @RequestParam(defaultValue = "price") String sortType,
                                                     @RequestParam(defaultValue = "true") boolean isAscending,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize){


        if(startDate.isBefore(LocalDate.now())) throw new InvalidInputException("Start date must be in the future");
        if(endDate.isBefore(LocalDate.now())) throw new InvalidInputException("End date must be in the future");
        if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) throw new InvalidInputException("Start date must be before end date");

        Page<AccommodationSearchDTO> pagedAccommodations = accommodationService.searchAccommodations(guestNum, address, startDate, endDate, amenities, accommodationType,
                minPrice, maxPrice, pageNo, pageSize, isAscending, sortType);


        return ResponseEntity.ok().body(pagedAccommodations);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping(path = "/owner-list")
    public ResponseEntity<?> getOwnerAccommodations(@AuthenticationPrincipal Owner owner,
                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                    @RequestParam(defaultValue = "10") int pageSize){

        Page<AccommodationViewDTO> accommodations = accommodationService.getOwnerAccommodations(owner.getId(), pageNo, pageSize);
        return ResponseEntity.ok().body(accommodations);
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


    @PreAuthorize("hasAuthority('GUEST')")
    @GetMapping(path = "/favorites")
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal Guest guest,
                                          @RequestParam(defaultValue = "0") int pageNo,
                                          @RequestParam(defaultValue = "10") int pageSize){
        Page<AccommodationViewDTO> favorites = accommodationService.getFavorites(guest.getId(), pageNo, pageSize);

        return ResponseEntity.ok().body(favorites);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PutMapping(path = "/{accommodationId}/reservation-auto-accept/{status}")
    public ResponseEntity<?> toggleAutoAccept(@PathVariable("accommodationId") Long accommodationId,
                                              @PathVariable("status") Boolean status,
                                              @AuthenticationPrincipal Owner owner){
        accommodationService.toggleAutoAccept(accommodationId, status, owner.getId());

        return new ResponseEntity<>(("Auto accept is now " + (status ? "ON" : "OFF")), HttpStatus.OK);
    }



}
