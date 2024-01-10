package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AccommodationRequestService {
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final AvailabilityRequestRepository availabilityRequestRepository;
    private final OwnerRepository ownerRepository;
    private final AvailabilityService availabilityService;
    private final AvailabilityRepository availabilityRepository;
    private final AccommodationRepository accommodationRepository;

    private static final String UPLOAD_DIR = "uploads";


    private List<Amenity> checkInputValues(AccommodationDTO accommodationDTO){

//        try {
//            //AccommodationType.valueOf(accommodationDTO.getAccommodationType());
//        } catch (HttpMessageNotReadableException e) {
//            throw new InvalidEnumValueException("Invalid accommodation value");
//        }
//
        List<Amenity> amenities = new ArrayList<>();
        for(Amenity amenityStr: accommodationDTO.getAmenities()){
                amenities.add(amenityStr);
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
            String relativePath = saveImage(owner.getEmail(), accommodationDTO.getName(), image, currentIndex + 1);
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
                .accommodationType(accommodationDTO.getAccommodationType())
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
                    .requestType(availDTO.getRequestType())
                    .build();
            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);
    }

    public void createEditAccommodationRequest(Integer ownerId, List<MultipartFile> images, AccommodationDTO accommodationDTO, Long accommodationId) {

        var ownerWrapper = ownerRepository.findOwnerById(ownerId);
        Owner owner = ownerWrapper.orElseThrow();

        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        var amenities = checkInputValues(accommodationDTO);

        if(availabilityService.reservationsNotEnded(accommodationId))
            throw new ReservationNotEndedException("One or more reservations for this availability period haven't ended yet");


        List<String> imagePaths = new ArrayList<>();
        for(MultipartFile image: images){
            int currentIndex = images.indexOf(image);
            String relativePath = saveImage(owner.getEmail(), accommodationDTO.getName(), image, currentIndex);
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
                .accommodationType(accommodationDTO.getAccommodationType())
                .autoAcceptEnabled(accommodationDTO.getAutoAcceptEnabled())
                .availabilityRequests(new ArrayList<AvailabilityRequest>())
                .owner(owner)
                .accommodation(accommodation)
                .status(RequestStatus.PENDING)
                .build();

        accommodationRequestRepository.save(accommodationRequest);

        for(AccommodationAvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){
            AccommodationAvailability accommodationAvailability = null;
            if(availDTO.getId() != 0) accommodationAvailability = availabilityRepository.findById(availDTO.getId()).get();

            var availabilityRequest = AvailabilityRequest.builder()
                    .startDate(availDTO.getStartDate())
                    .endDate(availDTO.getEndDate())
                    .cancelDeadline(availDTO.getCancellationDeadline())
                    .price(availDTO.getPrice())
                    .pricePerGuest(availDTO.getPricePerGuest())
                    .accommodationRequest(accommodationRequest)
                    .accommodationAvailability(accommodationAvailability)
                    .requestType(availDTO.getRequestType())
                    .build();

            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);

    }

    public String saveImage(String email, String accommodationName, MultipartFile file, Integer currentIndex){
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

    public void deleteImage(String filePath) {
        Path path = Paths.get(filePath);

        // Check if the file exists before attempting to delete
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File not found. Unable to delete.");
        }
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

    public void acceptRequest(Long requestId) {

        var accommodationRequestWrapper = accommodationRequestRepository.findById(requestId);
        if(accommodationRequestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        var accommodationRequest = accommodationRequestWrapper.get();

        accommodationRequest.setStatus(RequestStatus.ACCEPTED);
        accommodationRequestRepository.save(accommodationRequest);

        if(accommodationRequest.getAccommodation() == null){
            var accommdation = Accommodation.builder()
                    .name(accommodationRequest.getName())
                    .description(accommodationRequest.getDescription())
                    .address(accommodationRequest.getAddress())
                    .lat(accommodationRequest.getLat())
                    .lon(accommodationRequest.getLon())
                    .amenities(accommodationRequest.getAmenities())
                    .imagePaths(accommodationRequest.getImagePaths())
                    .minGuests(accommodationRequest.getMinGuests())
                    .maxGuests(accommodationRequest.getMaxGuests())
                    .accommodationType(accommodationRequest.getAccommodationType())
                    .autoAcceptEnabled(accommodationRequest.getAutoAcceptEnabled())

                    .availabilityList(new ArrayList<>())

                    .owner(accommodationRequest.getOwner())
                    .reservations(new ArrayList<>())
                    .build();

            accommodationRepository.save(accommdation);

            for(AvailabilityRequest availabilityRequest: accommodationRequest.getAvailabilityRequests()){
                var availability = AccommodationAvailability.builder()
                        .accommodation(accommdation)
                        .startDate(availabilityRequest.getStartDate())
                        .endDate(availabilityRequest.getEndDate())
                        .price(availabilityRequest.getPrice())
                        .cancelDeadline(availabilityRequest.getCancelDeadline())
                        .pricePerGuest(availabilityRequest.getPricePerGuest())
                        .build();

                availabilityRepository.save(availability);
                accommdation.getAvailabilityList().add(availability);
            }

            accommodationRepository.save(accommdation);

        }else{
            var accommodation = accommodationRequest.getAccommodation();
            accommodation.setName(accommodationRequest.getName());
            accommodation.setDescription(accommodationRequest.getDescription());
            accommodation.setAddress(accommodationRequest.getAddress());
            accommodation.setLat(accommodationRequest.getLat());
            accommodation.setLon(accommodationRequest.getLon());
            accommodation.setAmenities(accommodationRequest.getAmenities());
            accommodation.setMinGuests(accommodationRequest.getMinGuests());
            accommodation.setMaxGuests(accommodationRequest.getMaxGuests());
            accommodation.setAccommodationType(accommodationRequest.getAccommodationType());
            accommodation.setAutoAcceptEnabled(accommodationRequest.getAutoAcceptEnabled());



            for (String imagePath : accommodationRequest.getImagePaths()) {
                if (!accommodation.getImagePaths().contains(imagePath)) {
                    accommodation.getImagePaths().add(imagePath);
                }
            }

            List<String> imagesToRemove = new ArrayList<>();

            for (String imagePath : accommodation.getImagePaths()) {
                if (!accommodationRequest.getImagePaths().contains(imagePath)) {
                    imagesToRemove.add(imagePath);
                }
            }

            accommodation.getImagePaths().removeAll(imagesToRemove);
            for(String imagePath: imagesToRemove){
                deleteImage(imagePath);
            }

            accommodationRepository.save(accommodation);


            for(AvailabilityRequest ar: accommodationRequest.getAvailabilityRequests()){
                if (ar.getRequestType().equals(RequestType.CREATE)){
                 var availability = AccommodationAvailability.builder()
                         .accommodation(accommodation)
                         .pricePerGuest(ar.getPricePerGuest())
                         .price(ar.getPrice())
                         .cancelDeadline(ar.getCancelDeadline())
                         .startDate(ar.getStartDate())
                         .endDate(ar.getEndDate())
                         .build();

                 availabilityRepository.save(availability);
                 accommodation.getAvailabilityList().add(availability);
                 accommodationRepository.save(accommodation);
                }
                if(ar.getRequestType().equals(RequestType.EDIT)){

                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
                    var availability = availabilityWrapper.get();

                    availability.setPrice(ar.getPrice());
                    availability.setEndDate(ar.getEndDate());
                    availability.setStartDate(ar.getStartDate());
                    availability.setCancelDeadline(ar.getCancelDeadline());
                    availability.setPricePerGuest(ar.getPricePerGuest());

                    availabilityRepository.save(availability);
                }

                if(ar.getRequestType().equals(RequestType.DELETE)){
                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
                    var availability = availabilityWrapper.get();

                    availabilityRepository.delete(availability);
                }
            }

        }

    }
    public void declineRequest(Long requestId, String reason) {

        var accommodationRequestWrapper = accommodationRequestRepository.findById(requestId);
        if(accommodationRequestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        var accommodationRequest = accommodationRequestWrapper.get();

        accommodationRequest.setStatus(RequestStatus.REJECTED);
        accommodationRequest.setReason(reason);



        for(String imgPath: accommodationRequest.getImagePaths()){
            deleteImage(imgPath);
        }

    }
}
