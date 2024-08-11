package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.Admin;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRequestRepository;
import com.example.mobilnetestiranjebackend.repositories.AdminRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import com.example.mobilnetestiranjebackend.services.AccommodationRequestService;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import com.sendgrid.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accommodation-requests")
@RequiredArgsConstructor
public class AccommodationRequestController {
    private final AccommodationService accommodationService;
    private final AccommodationRequestService accommodationRequestService;
    private final AdminRepository adminRepository;
    private final OwnerRepository ownerRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                        @RequestPart("images") List<MultipartFile> images,
                                                        @AuthenticationPrincipal Owner owner) {
        accommodationRequestService.createAccommodationRequest(owner, images, accommodationDTO);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/accommodations/{accommodationId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createEditAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                            @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                            @AuthenticationPrincipal Owner owner,
                                                            @PathVariable("accommodationId") Long accommodationId) {

        accommodationRequestService.createEditAccommodationRequest(owner, images, accommodationDTO, accommodationId);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/{requestId}/{status}")
    public ResponseEntity<?> processAccommodationRequest(@PathVariable("requestId") Long requestId,
                                                        @PathVariable("status") RequestStatus status,
                                                        @RequestBody(required = false) String reason){

        if(status.equals(RequestStatus.ACCEPTED)) accommodationRequestService.acceptRequest(requestId);
        else if(status.equals(RequestStatus.REJECTED)) accommodationRequestService.declineRequest(requestId, reason);
        else throw new InvalidEnumValueException("Invalid status value");

        return ResponseEntity.ok().body("Successfully accepted accommodation request");
    }


    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> getAccommodationRequests(@AuthenticationPrincipal User user,
                                                      @RequestParam(defaultValue = "0") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize){

        return ResponseEntity.ok().body(accommodationRequestService.getAccommodationRequests(pageNo, pageSize, user));
    }

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('ADMIN')")
    @GetMapping("/{accommodationRequestId}")
    public ResponseEntity<?> getAccommodationRequest(@AuthenticationPrincipal User user,
                                                      @PathVariable("accommodationRequestId") Long requestId){

        return ResponseEntity.ok().body(accommodationRequestService.getAccommodationRequest(user,requestId));

    }



    @GetMapping(value = "/{accommodationRequestId}/images/{imageId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OWNER')")
    public @ResponseBody ResponseEntity<?> getImageWithMediaTypeRequest(@PathVariable("accommodationRequestId") Long accommodationRequestId,
                                                                        @PathVariable("imageId") Long imageId,
                                                                        @AuthenticationPrincipal User user) throws IOException {


        Optional<Admin> adminWrapper = adminRepository.findAdminById(user.getId());
        Optional<Owner> ownerWrapper = ownerRepository.findOwnerById(user.getId());

        if(ownerWrapper.isEmpty() && adminWrapper.isEmpty()) throw new InvalidAuthorizationException("You don't have authority for this action");


        var accommodationRequestWrapper = accommodationRequestRepository.findById(accommodationRequestId);
        if(accommodationRequestWrapper.isEmpty()) throw new InvalidInputException("Request with this id not found");
        var request = accommodationRequestWrapper.get();
        if(ownerWrapper.isPresent()) {
            Owner owner  = ownerWrapper.get();
            if(!Objects.equals(request.getOwner().getId(), owner.getId())) throw new InvalidAuthorizationException("You do not own this accommodation");
        }


        String foundImgPath = "";
        MediaType foundImgType = null;

        if(request.getAccommodation() != null){
            for(String imgPath: request.getAccommodation().getImagePaths()){
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
        }

        if(!request.getStatus().equals(RequestStatus.ACCEPTED)){
            for(String imgPath: request.getImagePathsNew()){
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

}
