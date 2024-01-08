package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidFileExtensionException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                 @RequestPart("photos") List<MultipartFile> images,
                                                 @AuthenticationPrincipal User user) {
        accommodationService.createAccommodationRequest(user.getId(), images, accommodationDTO);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }


    //@PreAuthorize("hasAuthority('OWNER')")
    @GetMapping(path = "/{accommodationId}")
    public ResponseEntity<?> getAccommodation(@PathVariable("accommodationId") Long accommodationId, @AuthenticationPrincipal User user){

        var accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!user.getEmail().equals(accommodation.getOwner().getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        var accommodationDTO = AccommodationDTO.builder()
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .address(accommodation.getAddress())
                .lat(accommodation.getLat())
                .lon(accommodation.getLon())
                .amenities(accommodation.getAmenities().stream().map(Amenity::name).collect(Collectors.toList()))
                .minGuests(accommodation.getMinGuests())
                .maxGuests(accommodation.getMaxGuests())
                .accommodationType(accommodation.getAccommodationType().toString())
                .autoAcceptEnabled(accommodation.getAutoAcceptEnabled())
                .availabilityList(new ArrayList<>())
                .imagePaths(accommodation.getImagePaths())
                .build();

        for(AccommodationAvailability a: accommodation.getAvailabilityList()){
            var availabilityDTO = AccommodationAvailabilityDTO.builder()
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

    //@PreAuthorize("hasAuthority('OWNER')")
    @GetMapping(
            value = "/{accommodationId}/image/{imageId}"
    )
    public @ResponseBody ResponseEntity<?> getImageWithMediaType(@PathVariable("accommodationId") Long accommodationId,
                                                      @PathVariable("imageId") Long imageId,
                                                      @AuthenticationPrincipal User user) throws IOException {

        var accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!user.getEmail().equals(accommodation.getOwner().getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");


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

    @PutMapping(path = "/{accommodationId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createEditAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                        @RequestPart("photos") List<MultipartFile> images,
                                                        @AuthenticationPrincipal User user,
                                                            @PathVariable("accommodationId") Long accommdationId) {

        accommodationService.createEditAccommodationRequest(user.getId(), images, accommodationDTO, accommdationId);
        return ResponseEntity.ok().body("Successfully created new accommodation request");

    }
}
