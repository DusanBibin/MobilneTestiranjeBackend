package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.*;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.helpers.PageConverter;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AccommodationRequestService {
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final AvailabilityRequestRepository availabilityRequestRepository;
    private final OwnerRepository ownerRepository;
    private final ReservationService reservationService;
    private final AvailabilityRepository availabilityRepository;
    private final AccommodationRepository accommodationRepository;
    private final AvailabilityService availabilityService;

    private static final String UPLOAD_DIR = "uploads";
    private final ReservationRepository reservationRepository;


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
                    throw new InvalidDateException("Availability with start date " + startDate1 + " and end date " + endDate1 + " interlaces with availability" +
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
                .imagePathsNew(imagePaths)
                .minGuests(accommodationDTO.getMinGuests())
                .maxGuests(accommodationDTO.getMaxGuests())
                .accommodationType(accommodationDTO.getAccommodationType())
                .autoAcceptEnabled(accommodationDTO.getAutoAcceptEnabled())
                .availabilityRequests(new ArrayList<AvailabilityRequest>())
                .owner(owner)
                .status(RequestStatus.PENDING)
                .build();

        accommodationRequest = accommodationRequestRepository.save(accommodationRequest);

        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){
            var availabilityRequest = AvailabilityRequest.builder()
                    .startDate(availDTO.getStartDate())
                    .endDate(availDTO.getEndDate())
                    .cancelDeadline(availDTO.getCancellationDeadline())
                    .price(availDTO.getPrice())
                    .pricePerGuest(availDTO.getPricePerGuest())
                    .requestType(RequestType.CREATE)
                    .build();
            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);
    }

    public void createEditAccommodationRequest(Owner owner, List<MultipartFile> images, AccommodationDTO accommodationDTO, Long accommodationId) {

        var accommodationWrapper = accommodationRepository.findAccommodationsByOwnerAndName(owner, accommodationDTO.getName());
        if(accommodationWrapper.isPresent() && !Objects.equals(accommodationWrapper.get().getId(), accommodationId)) throw new EntityAlreadyExistsException("You already have accommodation with this name");

        accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();



        if(!accommodation.getOwner().getEmail().equals(owner.getEmail())) throw new InvalidAuthorizationException("You don't own this accommodation");

        if(reservationService.reservationsNotEnded(accommodationId))
            throw new ReservationNotEndedException("You cannot change details if there are active reservations ");


        List<Amenity> amenities = new ArrayList<>();
        for(Amenity amenityStr: accommodationDTO.getAmenities()){
            amenities.add(amenityStr);
        }


        Map<Long, AvailabilityDTO> availReqMap = new HashMap<>();
        for(AvailabilityDTO a: accommodationDTO.getAvailabilityList()){
            availReqMap.put(a.getId(), a);
        }


        Map<Long, AvailabilityDTO> visited = new HashMap<>();

        for(AvailabilityDTO availReq: accommodationDTO.getAvailabilityList()){

            if(!availReq.getRequestType().equals(RequestType.DELETE)){
                for(Availability a: availabilityRepository.findAllByAccommodationId(accommodationId)){

                    if(a.getId() != availReq.getId() || visited.containsKey(a.getId())){
                        if(availReqMap.containsKey(a.getId())){

                            var reqPom = availReqMap.get(a.getId());
                            if(reqPom.getRequestType().equals(RequestType.EDIT) && !reqPom.getId().equals(availReq.getId())){

                                if(!((availReq.getStartDate().compareTo(reqPom.getStartDate()) < 0 && availReq.getEndDate().compareTo(reqPom.getStartDate()) < 0) ||
                                        (availReq.getStartDate().compareTo(reqPom.getEndDate()) > 0 && availReq.getEndDate().compareTo(reqPom.getEndDate()) > 0))){

                                    System.out.println("REQ POM: " + reqPom.getId());
                                    System.out.println("AVAIL REQ: " + availReq.getId());
                                    throw new InvalidInputException("Some date ranges are in conflict");
                                }


                            }

                        }else{
                            if(!((availReq.getStartDate().compareTo(a.getStartDate()) < 0 && availReq.getEndDate().compareTo(a.getStartDate()) < 0) ||
                                    (availReq.getStartDate().compareTo(a.getEndDate()) > 0 && availReq.getEndDate().compareTo(a.getEndDate()) > 0))){
                                System.out.println("A: " + a.getId());
                                System.out.println("AVAIL REQ: " + availReq.getId());
                                throw new InvalidInputException("Some date ranges are in conflict");
                            }
                        }

                    }

                }
                visited.put(availReq.getId(), availReq);
            }
        }







        List<String> imagePathsNew = new ArrayList<>();
        if(images != null){
            if(images.size() > 5) throw new TooManyFilesException("You can only upload 5 images");

            int currentIndex = 1;
            for(MultipartFile image: images){

                for(String imgPath: accommodation.getImagePaths()) {
                    String[] pathParts = imgPath.split("/");
                    System.out.println("OVO JE INDEKS");
                    System.out.println(pathParts);
                    String fileName = pathParts[3];
                    System.out.println("===========");
                    System.out.println(fileName);
                    System.out.println(fileName.charAt(0));
                    System.out.println(currentIndex);
                    System.out.println("===========");
                    if (Character.getNumericValue(fileName.charAt(0)) == currentIndex) currentIndex += 1;
                    System.out.println(currentIndex);
                }

                for(String imgPath: imagePathsNew) {
                    String[] pathParts = imgPath.split("/");

                    String fileName = pathParts[3];
                    if (Character.getNumericValue(fileName.charAt(0)) == currentIndex) currentIndex += 1;
                }

                checkExtension(image.getOriginalFilename());
                String relativePath = saveImage(owner.getEmail(), accommodation.getName(), image, currentIndex);
                imagePathsNew.add(relativePath);
            }
        }



        var accommodationRequest = AccommodationRequest.builder()
                .name(accommodationDTO.getName())
                .description(accommodationDTO.getDescription())
                .address(accommodationDTO.getAddress())
                .lat(accommodationDTO.getLat())
                .lon(accommodationDTO.getLon())
                .amenities(amenities)
                .imagePathsNew(imagePathsNew)
                .imagesToRemove(accommodationDTO.getImagesToDelete())
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

        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){

            Availability availability = null;

            RequestType type = RequestType.valueOf(availDTO.getRequestType().toString());
            System.out.println(type);
            if(type.equals(RequestType.EDIT) || type.equals(RequestType.DELETE)){
                var availabilityWrappper = availabilityRepository.findByIdAndAccommodationId(availDTO.getId(), accommodationId);
                System.out.println(availDTO.getId());
                System.out.println(accommodationDTO.getId());
                if(availabilityWrappper.isEmpty()) throw new InvalidInputException("Availability with this id doesn't exist");
                availability = availabilityWrappper.get();
            }

            var availabilityRequest = AvailabilityRequest.builder()
                    .startDate(availDTO.getStartDate())
                    .endDate(availDTO.getEndDate())
                    .cancelDeadline(availDTO.getCancellationDeadline())
                    .price(availDTO.getPrice())
                    .pricePerGuest(availDTO.getPricePerGuest())
                    .availability(availability)
                    .requestType(availDTO.getRequestType())
                    .build();

            availabilityRequestRepository.save(availabilityRequest);
            accommodationRequest.getAvailabilityRequests().add(availabilityRequest);
        }

        accommodationRequestRepository.save(accommodationRequest);

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
        System.out.println("Brisi " + filePath);
        String directory = "uploads/" + filePath;
        System.out.println(directory);
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



        if(accommodationRequest.getAccommodation() == null){
            var accommdation = Accommodation.builder()
                    .name(accommodationRequest.getName())
                    .description(accommodationRequest.getDescription())
                    .address(accommodationRequest.getAddress())
                    .lat(accommodationRequest.getLat())
                    .lon(accommodationRequest.getLon())
                    .amenities(new ArrayList<>(accommodationRequest.getAmenities()))
                    .imagePaths(new ArrayList<>(accommodationRequest.getImagePathsNew()))
                    .minGuests(accommodationRequest.getMinGuests())
                    .maxGuests(accommodationRequest.getMaxGuests())
                    .accommodationType(accommodationRequest.getAccommodationType())
                    .autoAcceptEnabled(accommodationRequest.getAutoAcceptEnabled())

                    .availabilityList(new ArrayList<>())

                    .owner(accommodationRequest.getOwner())
                    .reservations(new ArrayList<>())
                    .accommodationReviews(new ArrayList<>())
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
                System.out.println("jesmo li usli u if za kreiranje requeqesta novog");
                accommdation.getAvailabilityList().add(availability);
            }

            accommodationRepository.save(accommdation);

        }else{
            var accommodation = accommodationRequest.getAccommodation();

            if(!accommodation.getName().equals(accommodationRequest.getName())){
                String[] pathParts = accommodation.getImagePaths().get(0).split("/");
                File oldFolder = new File("uploads/" + pathParts[1] + "/" + pathParts[2]);
                File newFolder = new File("uploads/" + pathParts[1] + "/" + accommodationRequest.getName());
                if (oldFolder.renameTo(newFolder)) {
                    System.out.println("Folder renamed successfully!");
                } else {
                    System.err.println("Failed to rename folder.");
                }

                for (int i = 0; i < accommodation.getImagePaths().size(); i++) {
                    String path = accommodation.getImagePaths().get(i);
                    String[] split = path.split("/");
                    accommodation.getImagePaths().set(i, split[1] + "/" + accommodationRequest.getName() + "/" + split[3]);
                }
            }


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




            for (String imagePath : accommodationRequest.getImagePathsNew()) {
                if (!accommodation.getImagePaths().contains(imagePath)) {
                    accommodation.getImagePaths().add(imagePath);
                }
            }

//
//            List<String> imagesToRemove = new ArrayList<>();
//            for (String imagePath : accommodation.getImagePaths()) {
//                if (!accommodationRequest.getImagePathsNew().contains(imagePath)) {
//                    imagesToRemove.add(imagePath);
//                }
//            }


            accommodation.getImagePaths().removeAll(accommodationRequest.getImagesToRemove());
            for(String imagePath: accommodationRequest.getImagesToRemove()){
                System.out.println("DA LI OVDE ULAZIMO UOPSTE U PM");
                System.out.println(imagePath);
                accommodation.getImagePaths().remove("/" + accommodation.getOwner().getEmail() + "/" + accommodation.getName() + "/" + imagePath);
                System.out.println(accommodation.getOwner().getEmail() + "/" + accommodation.getName() + "/" + imagePath);
                accommodation = accommodationRepository.save(accommodation);
                deleteImage(accommodation.getOwner().getEmail() + "/" + accommodation.getName() + "/" + imagePath);
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




            for(AvailabilityRequest ar: accommodationRequest.getAvailabilityRequests()){
                if (ar.getRequestType().equals(RequestType.CREATE)){
                     var availability = Availability.builder()
                             .accommodation(accommodation)
                             .pricePerGuest(ar.getPricePerGuest())
                             .price(ar.getPrice())
                             .cancelDeadline(ar.getCancelDeadline())
                             .startDate(ar.getStartDate())
                             .endDate(ar.getEndDate())
                             .build();

                     availabilityRepository.save(availability);
                     System.out.println("jesmo li usli u kreiranje create novog availabilitija");
                     accommodation.getAvailabilityList().add(availability);
                     accommodationRepository.save(accommodation);
                }else if(ar.getRequestType().equals(RequestType.EDIT)){

                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
                    var availability = availabilityWrapper.get();

                    availability.setPrice(ar.getPrice());
                    availability.setEndDate(ar.getEndDate());
                    availability.setStartDate(ar.getStartDate());
                    availability.setCancelDeadline(ar.getCancelDeadline());
                    availability.setPricePerGuest(ar.getPricePerGuest());

                    System.out.println("jesmo li usli u kreiranje novog edit availabilitija");
                    availabilityRepository.save(availability);
                }else{

                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
                    var availability = availabilityWrapper.get();

//                    ar.setAvailability(null);
//                    ar = availabilityRequestRepository.save(ar);
                    System.out.println("AR JE: ");
                    System.out.println(ar.getAvailability() == null);


//                    availability.setAccommodation(null);
//                    availability = availabilityRepository.save(availability);
                    System.out.println("availability je ");
                    System.out.println(availability.getAccommodation() == null);

                    accommodation.getAvailabilityList().remove(availability);
                    accommodation = accommodationRepository.save(accommodation);
                    System.out.println("Avail za ovaj accom su sada: ");
                    for(Availability a: accommodation.getAvailabilityList()){
                        System.out.println(a.getId());
                    }
                    List<AvailabilityRequest> availabilityRequests = availabilityRequestRepository.findByAvailability(availability);
                    for (AvailabilityRequest request : availabilityRequests) {
                        request.setAvailability(null);
                        availabilityRequestRepository.save(request);
                    }

                    List<Reservation> reservations = reservationRepository.findByAvailability(availability);
                    for(Reservation reservation: reservations){
                        reservation.setAvailability(null);
                        reservationRepository.save(reservation);
                    }
                    System.out.println("da li smo usli u brisanje availabilitija");
                    availabilityRepository.delete(availability);
                }
            }
        }

    }
    public void declineRequest(Long requestId, String reason) {

        if(reason.isEmpty()) throw new InvalidInputException("No reason provided");

        var accommodationRequestWrapper = accommodationRequestRepository.findById(requestId);
        if(accommodationRequestWrapper.isEmpty()) throw new NonExistingEntityException("This request doesn't exist");
        var accommodationRequest = accommodationRequestWrapper.get();

        if(accommodationRequest.getStatus() != RequestStatus.PENDING) throw new InvalidEnumValueException("You can only reject a pending request");

        accommodationRequest.setStatus(RequestStatus.REJECTED);
        accommodationRequest.setReason(reason);

        for(String imgPath: accommodationRequest.getImagePathsNew()){
            deleteImage(imgPath);
        }

    }

    public Page<AccommodationRequestPreviewDTO> getAccommodationRequests(int pageNum, int pageSize, Owner owner) {

        List<AccommodationRequest> requests = accommodationRequestRepository.findAllByOwnerId(owner.getId());
        List<AccommodationRequestPreviewDTO> convertedList = new ArrayList<>(requests.stream().map(a -> {
            AccommodationRequestPreviewDTO ar = new AccommodationRequestPreviewDTO();
            ar.setRequestId(a.getId());
            ar.setReason(a.getReason());
            ar.setAccommodationName(a.getName());
            ar.setStatus(a.getStatus());
            ar.setAccommodationAddress(a.getAddress());
            ar.setRequestType((a.getAccommodation() == null) ? RequestType.CREATE : RequestType.EDIT);
            if(a.getAccommodation() != null){
                ar.setExistingAccommodationName(a.getAccommodation().getName());
                ar.setExistingAddress(a.getAccommodation().getAddress());
            }

            return ar;
        }).toList());

        return PageConverter.convertListToPage(pageNum, pageSize, convertedList);
    }

    public AccommodationDifferencesDTO getAccommodationRequest(Owner owner, Long requestId) {

        var requestWrapper = accommodationRequestRepository.findByOwnerIdAndId(requestId, owner.getId());
        if(requestWrapper.isEmpty()) throw new NonExistingEntityException("This request does not exist");
        AccommodationRequest request = requestWrapper.get();
        Accommodation accommodation = request.getAccommodation();

        AccommodationDTOEdit requestDTONew = new AccommodationDTOEdit();
        requestDTONew.setAddress(request.getAddress());
        requestDTONew.setDescription(request.getDescription());
        requestDTONew.setName(request.getName());
        requestDTONew.setAmenities(request.getAmenities());
        requestDTONew.setLat(request.getLat());
        requestDTONew.setLon(request.getLon());
        requestDTONew.setAccommodationType(request.getAccommodationType());
        requestDTONew.setAutoAcceptEnabled(request.getAutoAcceptEnabled());
        requestDTONew.setMinGuests(request.getMinGuests());
        requestDTONew.setMaxGuests(request.getMaxGuests());




        List<AvailabilityDTO> newAvailabilities = new ArrayList<>();
        for(AvailabilityRequest ar: request.getAvailabilityRequests()){
            AvailabilityDTO avail = new AvailabilityDTO();
            if(ar.getAvailability() != null) avail.setId(ar.getAvailability().getId());
            else avail.setId(0L);
            avail.setStartDate(ar.getStartDate());
            avail.setEndDate(ar.getEndDate());
            avail.setPrice(ar.getPrice());
            avail.setCancellationDeadline(ar.getCancelDeadline());
            avail.setPricePerGuest(ar.getPricePerGuest());
            avail.setRequestType(ar.getRequestType());
            newAvailabilities.add(avail);
        }

        List<String> imagesToAdd = new ArrayList<>();
        for(int i = 0; i < request.getImagePathsNew().size(); i++){
            String[] split = request.getImagePathsNew().get(i).split("/");
            imagesToAdd.add(split[3]);
        }


        AccommodationDifferencesDTO requestDifferences = new AccommodationDifferencesDTO();
        requestDifferences.setRequestAccommodationInfo(requestDTONew);
        requestDifferences.setRequestAvailabilities(newAvailabilities);
        requestDifferences.setImagesToAdd(imagesToAdd);
        requestDifferences.setImagesToRemove(request.getImagesToRemove());


        if(accommodation != null ) {


            AccommodationDTOEdit requestDTOOld = new AccommodationDTOEdit();
            requestDTOOld.setAddress(accommodation.getAddress());
            requestDTOOld.setDescription(accommodation.getDescription());
            requestDTOOld.setName(accommodation.getName());
            requestDTOOld.setAmenities(accommodation.getAmenities());
            requestDTOOld.setLat(accommodation.getLat());
            requestDTOOld.setLon(accommodation.getLon());
            requestDTOOld.setAccommodationType(accommodation.getAccommodationType());
            requestDTOOld.setAutoAcceptEnabled(accommodation.getAutoAcceptEnabled());
            requestDTOOld.setMinGuests(accommodation.getMinGuests());
            requestDTOOld.setMaxGuests(accommodation.getMaxGuests());

            List<AvailabilityDTO> oldAvailabilities = new ArrayList<>();
            for (Availability a : accommodation.getAvailabilityList()) {
                AvailabilityDTO avail = new AvailabilityDTO();
                avail.setId(a.getId());
                avail.setStartDate(a.getStartDate());
                avail.setEndDate(a.getEndDate());
                avail.setPrice(a.getPrice());
                avail.setCancellationDeadline(a.getCancelDeadline());
                avail.setPricePerGuest(a.getPricePerGuest());
                oldAvailabilities.add(avail);
            }

            List<String> images = new ArrayList<>();
            for (int i = 0; i < accommodation.getImagePaths().size(); i++) {
                String[] split = accommodation.getImagePaths().get(i).split("/");
                images.add(split[3]);
            }

            requestDifferences.setAccommodationInfo(requestDTOOld);
            requestDifferences.setAvailabilities(oldAvailabilities);
            requestDifferences.setCurrentImages(images);
        }



        return requestDifferences;
    }

}
