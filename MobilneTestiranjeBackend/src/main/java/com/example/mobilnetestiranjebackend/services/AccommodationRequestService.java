package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.*;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.helpers.PageConverter;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.repositories.*;
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
    private final AdminRepository adminRepository;


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
                if(i != j){
                    var avail1 = accommodationDTO.getAvailabilityList().get(i);
                    var avail2 = accommodationDTO.getAvailabilityList().get(j);

                    var startDate1 = avail1.getStartDate();
                    var endDate1 = avail1.getEndDate();
                    var startDate2 = avail2.getStartDate();
                    var endDate2 = avail2.getEndDate();


                    if(!(startDate1.isBefore(startDate2) && endDate1.isBefore(startDate2) ||
                            startDate1.isAfter(endDate2) && endDate1.isAfter(endDate2))){
                        throw new InvalidDateException("Availability with start date " + startDate1 + " and end date " + endDate1 + " interferes with availability" +
                                " with start date " + startDate2 + " and end date " + endDate2);
                    }
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


        boolean differencesPresent = true;



        var accommodationWrapper = accommodationRepository.findAccommodationsByOwnerAndName(owner, accommodationDTO.getName());
        if(accommodationWrapper.isPresent() && !Objects.equals(accommodationWrapper.get().getId(), accommodationId)) throw new EntityAlreadyExistsException("You already have accommodation with this name");

        accommodationWrapper = accommodationRepository.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("The accommodation with this id does not exist");
        var accommodation = accommodationWrapper.get();



        var accommodationRequestWrapper = accommodationRequestRepository.findPendingRequestByAccommodationId(owner.getId(), accommodationId);
        if(accommodationRequestWrapper.isPresent()) throw new InvalidInputException("You already have pending request for this reservation");

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

        differencesPresent = checkGeneralInfo(accommodation, accommodationDTO, images);
        if(!differencesPresent) throw new InvalidInputException("There need to be differences to create new edit request");


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

                                     throw new InvalidInputException("Some date ranges are in conflict");
                                }


                            }

                        }else{
                            if(!((availReq.getStartDate().compareTo(a.getStartDate()) < 0 && availReq.getEndDate().compareTo(a.getStartDate()) < 0) ||
                                    (availReq.getStartDate().compareTo(a.getEndDate()) > 0 && availReq.getEndDate().compareTo(a.getEndDate()) > 0))){
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

                    String fileName = pathParts[3];

                    if (Character.getNumericValue(fileName.charAt(0)) == currentIndex) currentIndex += 1;

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
                .availabilityRequests(new ArrayList<AvailabilityRequest>())
                .owner(owner)
                .accommodation(accommodation)
                .status(RequestStatus.PENDING)
                .build();

        accommodationRequestRepository.save(accommodationRequest);

        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){

            Availability availability = null;

            RequestType type = RequestType.valueOf(availDTO.getRequestType().toString());

            if(type.equals(RequestType.EDIT) || type.equals(RequestType.DELETE)){
                var availabilityWrappper = availabilityRepository.findByIdAndAccommodationId(availDTO.getId(), accommodationId);

                if(availabilityWrappper.isEmpty()) throw new InvalidInputException("This date range isn't available");
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

    private boolean checkGeneralInfo(Accommodation accommodation, AccommodationDTO accommodationDTO, List<MultipartFile> images) {

        if(!accommodation.getName().equals(accommodationDTO.getName())) return true;

        if(!accommodation.getDescription().equals(accommodationDTO.getDescription())) return true;

        if(!accommodation.getAddress().equals(accommodationDTO.getAddress())) return true;

        if(!accommodation.getLat().equals(accommodationDTO.getLat())) return true;

        if(!accommodation.getLon().equals(accommodationDTO.getLon())) return true;

        if(!(new HashSet<>(accommodation.getAmenities())).equals(new HashSet<>(accommodationDTO.getAmenities()))) return true;

        if(!accommodation.getMinGuests().equals(accommodationDTO.getMinGuests())) return true;

        if(!accommodation.getMaxGuests().equals(accommodationDTO.getMaxGuests())) return true;

        if(!accommodation.getAccommodationType().equals(accommodationDTO.getAccommodationType())) return true;

        if(!accommodation.getAutoAcceptEnabled().equals(accommodationDTO.getAutoAcceptEnabled())) return true;


        for(AvailabilityDTO availDTO: accommodationDTO.getAvailabilityList()){

            if(availDTO.getRequestType().equals(RequestType.EDIT) || availDTO.getRequestType().equals(RequestType.DELETE)){

                var availabilityWrapper = availabilityRepository.findByIdAndAccommodationId(availDTO.getId(), accommodation.getId());
                if(availabilityWrapper.isEmpty()) throw new InvalidInputException("Availability not found");
                var availability = availabilityWrapper.get();

                if(!availability.getPricePerGuest().equals(availDTO.getPricePerGuest())) return true;

                if(!availability.getStartDate().equals(availDTO.getStartDate())) return true;

                if(!availability.getEndDate().equals(availDTO.getEndDate())) return true;

                if(!availability.getCancelDeadline().equals(availDTO.getCancellationDeadline())) return true;

                if(!availability.getPrice().equals(availDTO.getPrice())) return true;

            }
            
        }


        if(images == null && accommodationDTO.getImagesToDelete().isEmpty()) return true;

        return false;
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

        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return relativePath + "/" + filePath.getFileName();
    }

    public void deleteImage(String filePath) {

        String directory = "uploads/" + filePath;

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
                    .autoAcceptEnabled(false)
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
                    System.out.println("Error with renaming folder");
                }

                for (int i = 0; i < accommodation.getImagePaths().size(); i++) {
                    String path = accommodation.getImagePaths().get(i);
                    String[] split = path.split("/");
                    accommodation.getImagePaths().set(i, "/" + split[1] + "/" + accommodationRequest.getName() + "/" + split[3]);
                }
            }

            for (String imagePath : accommodationRequest.getImagePathsNew()) {

                if (!accommodation.getImagePaths().contains(imagePath)) {

                    if(!accommodation.getName().equals(accommodationRequest.getName())){
                         String[] split = imagePath.split("/");

                        String newPath = "/" + split[1] + "/" + accommodationRequest.getName() + "/" + split[3];
                        accommodation.getImagePaths().add(newPath);


                    }else {accommodation.getImagePaths().add(imagePath); }
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






//
//            List<String> imagesToRemove = new ArrayList<>();
//            for (String imagePath : accommodation.getImagePaths()) {
//                if (!accommodationRequest.getImagePathsNew().contains(imagePath)) {
//                    imagesToRemove.add(imagePath);
//                }
//            }


            accommodation.getImagePaths().removeAll(accommodationRequest.getImagesToRemove());
            for(String imagePath: accommodationRequest.getImagesToRemove()){
                accommodation.getImagePaths().remove("/" + accommodation.getOwner().getEmail() + "/" + accommodation.getName() + "/" + imagePath);
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

                           availabilityRepository.save(availability);
                }else{

                    var availabilityWrapper = availabilityRepository.findById(ar.getId());
                    if(availabilityWrapper.isEmpty()) throw new NonExistingEntityException("Availability doesn't exist");
                    var availability = availabilityWrapper.get();

//                    ar.setAvailability(null);
//                    ar = availabilityRequestRepository.save(ar);



//                    availability.setAccommodation(null);
//                    availability = availabilityRepository.save(availability);


                    accommodation.getAvailabilityList().remove(availability);
                    accommodation = accommodationRepository.save(accommodation);

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
        accommodationRequestRepository.save(accommodationRequest);
    }

    public Page<AccommodationRequestPreviewDTO> getAccommodationRequests(int pageNum, int pageSize, User user) {

        Optional<Owner> ownerWrapper = ownerRepository.findOwnerById(user.getId());
        Optional<Admin> adminWrapper = adminRepository.findAdminById(user.getId());

        List<AccommodationRequest> requests = null;

        if(ownerWrapper.isPresent()){
            Owner owner = ownerWrapper.get();
            requests = accommodationRequestRepository.findAllByOwnerId(owner.getId());


        }

        if(adminWrapper.isPresent()){
            requests = accommodationRequestRepository.findAll();
        }

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

    public AccommodationDifferencesDTO getAccommodationRequest(User user, Long requestId) {

        Optional<AccommodationRequest> requestWrapper = Optional.empty();

        var ownerWrapper = ownerRepository.findOwnerById(user.getId());
        if(ownerWrapper.isPresent()){
            requestWrapper = accommodationRequestRepository.findByOwnerIdAndId(requestId, ownerWrapper.get().getId());
        }

        var adminWrapper = adminRepository.findAdminById(user.getId());
        if(adminWrapper.isPresent()){
            requestWrapper = accommodationRequestRepository.findById(requestId);
        }

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
        requestDifferences.setEmail(request.getOwner().getEmail());
        requestDifferences.setFullName(request.getOwner().getFirstName() + " " + request.getOwner().getLastname());

        List<AvailabilityDTO> oldAvailabilities = new ArrayList<>();
        List<String> images = new ArrayList<>();

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


            for (int i = 0; i < accommodation.getImagePaths().size(); i++) {
                String[] split = accommodation.getImagePaths().get(i).split("/");
                images.add(split[3]);
            }
            requestDifferences.setAccommodationInfo(requestDTOOld);
        }

        requestDifferences.setAvailabilities(oldAvailabilities);
        requestDifferences.setCurrentImages(images);
        requestDifferences.setStatus(request.getStatus());
        requestDifferences.setReason(request.getReason());

        return requestDifferences;
    }


}
