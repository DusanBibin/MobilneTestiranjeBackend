package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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


    private void checkInputValues(AccommodationDTO accommodationDTO){

//        try {
//            //AccommodationType.valueOf(accommodationDTO.getAccommodationType());
//        } catch (HttpMessageNotReadableException e) {
//            throw new InvalidEnumValueException("Invalid accommodation value");
//        }
//

        for(AvailabilityDTO accAvail: accommodationDTO.getAvailabilityList()){
            if(accAvail.getEndDate().isBefore(accAvail.getStartDate()))
                throw new InvalidDateException("End date cannot be before start date");
            if(accAvail.getCancellationDeadline().isAfter(accAvail.getStartDate()) ||
                    accAvail.getCancellationDeadline().isEqual(accAvail.getStartDate()))
                throw new InvalidDateException("Cancellation date cannot be after start date");
        }


        for(int i = 0; i < accommodationDTO.getAvailabilityList().size(); i++){
            for(int j = i + 1; j < accommodationDTO.getAvailabilityList().size(); j++){
                var avail1 = accommodationDTO.getAvailabilityList().get(i);
                var avail2 = accommodationDTO.getAvailabilityList().get(j);

                var startDate1 = avail1.getStartDate();
                var endDate1 = avail1.getEndDate();
                var startDate2 = avail2.getStartDate();
                var endDate2 = avail2.getEndDate();


                if(!(startDate1.isBefore(startDate2) && endDate1.isBefore(startDate2) ||
                        startDate1.isAfter(endDate2) && endDate1.isAfter(endDate2))){
                    throw new InvalidDateException("Availability with start date " + startDate1 + " and end date " + endDate1 + " interlaps with availability" +
                            " with start date " + startDate2 + " and end date " + endDate2);
                }
            }
        }
    }
    public void createAccommodationRequest(Owner owner, List<MultipartFile> images, AccommodationDTO accommodationDTO){

        var accommodationWrapper = accommodationRepository.findAccommodationsByOwnerAndName(owner, accommodationDTO.getName());
        if(accommodationWrapper.isPresent()) throw new EntityAlreadyExistsException("You already have accommodation with this name");

        checkInputValues(accommodationDTO);

        List<Amenity> amenities = new ArrayList<>();
        for(Amenity amenityStr: accommodationDTO.getAmenities()){
            amenities.add(amenityStr);
        }


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

        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){
            var availabilityRequest = AvailabilityRequest.builder()
                    .startDate(availDTO.getStartDate())
                    .endDate(availDTO.getEndDate())
                    .cancelDeadline(availDTO.getCancellationDeadline())
                    .price(availDTO.getPrice())
                    .pricePerGuest(availDTO.getPricePerGuest())
                    .build();
            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);
    }

    public void createEditAccommodationRequest(Owner owner, List<MultipartFile> images, AccommodationDTO accommodationDTO, Long accommodationId) {



        var accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();

        if(!accommodation.getOwner().getEmail().equals(owner.getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        List<Amenity> amenities = new ArrayList<>();
        for(Amenity amenityStr: accommodationDTO.getAmenities()){
            amenities.add(amenityStr);
        }


//        for(AvailabilityDTO avail :accommodationDTO.getAvailabilityList()){
//            if(availabilityService.availabilityRangeTaken(accommodationId, avail.getStartDate(), avail.getEndDate(), avail.getId()))
//                throw new InvalidDateException("There is already availability period that interferes with this period");
//        }

        if(availabilityService.reservationsNotEnded(accommodationId))
            throw new ReservationNotEndedException("You cannot change details if there are active reservations ");




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

//        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){
//            Availability availability = null;
//            if(availDTO.getId() != 0) availability = availabilityRepository.findById(availDTO.getId()).get();
//
//            var availabilityRequest = AvailabilityRequest.builder()
//                    .startDate(availDTO.getStartDate())
//                    .endDate(availDTO.getEndDate())
//                    .cancelDeadline(availDTO.getCancellationDeadline())
//                    .price(availDTO.getPrice())
//                    .pricePerGuest(availDTO.getPricePerGuest())
//                    .availability(availability)
//                    .build();
//
//            availabilityRequestRepository.save(availabilityRequest);
//            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
//        }

    }

    public String saveImage(String email, String accommodationName, MultipartFile file, int currentIndex){
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
        System.out.println("Brisi" + filePath);
        String directory = "uploads" + filePath;
        Path path = Paths.get(directory);
        // Check if the file exists before attempting to delete
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                Path parentFolder = path.getParent();
                if(isDirEmpty(parentFolder)) Files.delete(parentFolder);

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

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    public void acceptRequest(Long requestId) {

        var accommodationRequestWrapper = accommodationRequestRepository.findById(requestId);
        if(accommodationRequestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        var accommodationRequest = accommodationRequestWrapper.get();

        if(accommodationRequest.getStatus() != RequestStatus.PENDING) throw new InvalidEnumValueException("You can only accept a pending request");


        accommodationRequest.setStatus(RequestStatus.ACCEPTED);
        accommodationRequestRepository.save(accommodationRequest);


        System.out.println(accommodationRequest.getAccommodation().getDescription());

        if(accommodationRequest.getAccommodation() == null){
            var accommdation = Accommodation.builder()
                    .name(accommodationRequest.getName())
                    .description(accommodationRequest.getDescription())
                    .address(accommodationRequest.getAddress())
                    .lat(accommodationRequest.getLat())
                    .lon(accommodationRequest.getLon())
                    .amenities(new ArrayList<>(accommodationRequest.getAmenities()))
                    .imagePaths(new ArrayList<>(accommodationRequest.getImagePaths()))
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
                var availability = Availability.builder()
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
            accommodation.setAmenities(new ArrayList<>(accommodationRequest.getAmenities()));
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

//            List<Long> idsToDelete = new ArrayList<>();
//            for(Availability ar: accommodation.getAvailabilityList()){
//
//                boolean isFound = false;
//
//                for(AvailabilityRequest ar1 : accommodationRequest.getAvailabilityRequests()){
//                    if(Objects.equals(ar.getId(), ar1.getId())){ isFound = true; break; }
//                }
//
//                if(!isFound) idsToDelete.add(ar.getId());
//
//            }
//
//            for(Long id: idsToDelete){
//
//                var availabilityWrapper = availabilityRepository.findById(id);
//                if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
//                var availability = availabilityWrapper.get();
//
//                availabilityRepository.delete(availability);
//            }
//
//
//
//
//            for(AvailabilityRequest ar: accommodationRequest.getAvailabilityRequests()){
//                if (ar.getId() == 0){
//                     var availability = Availability.builder()
//                             .accommodation(accommodation)
//                             .pricePerGuest(ar.getPricePerGuest())
//                             .price(ar.getPrice())
//                             .cancelDeadline(ar.getCancelDeadline())
//                             .startDate(ar.getStartDate())
//                             .endDate(ar.getEndDate())
//                             .build();
//
//                     availabilityRepository.save(availability);
//                     accommodation.getAvailabilityList().add(availability);
//                     accommodationRepository.save(accommodation);
//                }else{
//
//                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
//                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
//                    var availability = availabilityWrapper.get();
//
//                    availability.setPrice(ar.getPrice());
//                    availability.setEndDate(ar.getEndDate());
//                    availability.setStartDate(ar.getStartDate());
//                    availability.setCancelDeadline(ar.getCancelDeadline());
//                    availability.setPricePerGuest(ar.getPricePerGuest());
//
//                    availabilityRepository.save(availability);
//
//                }
//
//
//
//
//            }





        }

    }
    public void declineRequest(Long requestId, String reason) {

        var accommodationRequestWrapper = accommodationRequestRepository.findById(requestId);
        if(accommodationRequestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        var accommodationRequest = accommodationRequestWrapper.get();

        if(accommodationRequest.getStatus() != RequestStatus.PENDING) throw new InvalidEnumValueException("You can only reject a pending request");

        accommodationRequest.setStatus(RequestStatus.REJECTED);
        accommodationRequest.setReason(reason);

        for(String imgPath: accommodationRequest.getImagePaths()){
            deleteImage(imgPath);
        }

    }
}
