package com.example.mobilnetestiranjebackend.controller;

import com.example.mobilnetestiranjebackend.DTOs.*;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@TestPropertySource(
//        locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AccommodationRequestControllerTest {

    private final String BASE_PATH = "http://localhost:8080/api/v1/accommodation-request/";
    private static final String OWNER_EMAIL = "probamejl@gmail.com";
    private static final String OWNER_PASSWORD = "NekaSifra123";
    private String ownerToken = null;

    private static final String GUEST_EMAIL = "probamejl1@gmail.com";
    private static final String GUEST_PASSWORD = "NekaSifra123";
    private String guestToken = null;


    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate ownerRestTemplate, guestRestTemplate;

    HttpHeaders headers = new HttpHeaders();


    public void setInitialData(){
        headers.setContentType(MediaType.APPLICATION_JSON);
        loginAsDriver();
        loginAsPassenger();
        createRestTemplatesForUsers();

    }

    private void loginAsDriver() {
        HttpEntity<AuthenticationRequestDTO> ownerLogin = new HttpEntity<>(new AuthenticationRequestDTO(OWNER_EMAIL, OWNER_PASSWORD), headers);

        ResponseEntity<AuthenticationResponseDTO> driverResponse = restTemplate
                .exchange("/api/v1/auth/authenticate",
                        HttpMethod.POST,
                        ownerLogin,
                        new ParameterizedTypeReference<AuthenticationResponseDTO>() {
                        });

        this.ownerToken = driverResponse.getBody().getToken();
    }

    private void loginAsPassenger() {
        HttpEntity<AuthenticationRequestDTO> guestLogin = new HttpEntity<>(new AuthenticationRequestDTO(GUEST_EMAIL, GUEST_PASSWORD), headers);

        ResponseEntity<AuthenticationResponseDTO> passengerResponse = restTemplate
                .exchange("/api/v1/auth/authenticate",
                        HttpMethod.POST,
                        guestLogin,
                        new ParameterizedTypeReference<AuthenticationResponseDTO>() {
                        });

        this.guestToken = passengerResponse.getBody().getToken();
    }

    private void createRestTemplatesForUsers() {
        RestTemplateBuilder builder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.ownerToken);
            return execution.execute(request, body);
        }));
        this.ownerRestTemplate = new TestRestTemplate(builder);

        RestTemplateBuilder passBuilder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.guestToken);
            return execution.execute(request, body);
        }));

        this.guestRestTemplate = new TestRestTemplate(passBuilder);
    }

    @Test
    public void shouldThrowNonExistingAccommodationIdError(){
        setInitialData();


        AccommodationAvailabilityDTO avail = AccommodationAvailabilityDTO.builder()
                .startDate(LocalDate.now().plusDays(60))
                .endDate(LocalDate.now().plusDays(70))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();


        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .amenities(Arrays.asList(Amenity.WIFI))
                .address("neka adresa")
                .maxGuests(1L)
                .minGuests(1L)
                .name("neka akom")
                .description("neka deskripcija")
                .lat(45.0)
                .lon(45.0)
                .accommodationType(AccommodationType.STUDIO)
                .autoAcceptEnabled(false)
                .availabilityList(List.of(avail))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccommodationDTO> requestEntity = new HttpEntity<>(accommodationDTO, headers);




        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + "edit/accommodation/" + 40L,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );




        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(acceptResponse.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String message = jsonNode.get("message").asText();


        System.out.println(acceptResponse);

        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "The accommodation with this id does not exist");
    }

    @Test
    public void shouldThrowCancellationDateError(){
        setInitialData();


        AccommodationAvailabilityDTO avail = AccommodationAvailabilityDTO.builder()
                .startDate(LocalDate.now().plusDays(0))
                .endDate(LocalDate.now().plusDays(70))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();


        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .amenities(Arrays.asList(Amenity.WIFI))
                .address("neka adresa")
                .maxGuests(1L)
                .minGuests(1L)
                .name("neka akom")
                .description("neka deskripcija")
                .lat(45.0)
                .lon(45.0)
                .accommodationType(AccommodationType.STUDIO)
                .autoAcceptEnabled(false)
                .availabilityList(List.of(avail))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccommodationDTO> requestEntity = new HttpEntity<>(accommodationDTO, headers);




        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + "edit/accommodation/" + 1L,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );




        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(acceptResponse.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String message = jsonNode.get("message").asText();


        System.out.println(acceptResponse);

        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "Cancellation date cannot be after start date");
    }
    @Test
    public void shouldThrowEndDateError(){
        setInitialData();


        AccommodationAvailabilityDTO avail = AccommodationAvailabilityDTO.builder()
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(1))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();


        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .amenities(Arrays.asList(Amenity.WIFI))
                .address("neka adresa")
                .maxGuests(1L)
                .minGuests(1L)
                .name("neka akom")
                .description("neka deskripcija")
                .lat(45.0)
                .lon(45.0)
                .accommodationType(AccommodationType.STUDIO)
                .autoAcceptEnabled(false)
                .availabilityList(List.of(avail))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccommodationDTO> requestEntity = new HttpEntity<>(accommodationDTO, headers);




        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + "edit/accommodation/" + 1L,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(acceptResponse.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String message = jsonNode.get("message").asText();


        System.out.println(acceptResponse);

        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "End date cannot be before start date");
    }


    @Test
    public void shouldThrowConflictedNewAvailabilities(){
        setInitialData();


        AccommodationAvailabilityDTO avail = AccommodationAvailabilityDTO.builder()
                .startDate(LocalDate.now().plusDays(40))
                .endDate(LocalDate.now().plusDays(70))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();

        AccommodationAvailabilityDTO avail1 = AccommodationAvailabilityDTO.builder()
                .startDate(LocalDate.now().plusDays(40))
                .endDate(LocalDate.now().plusDays(70))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();


        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .amenities(Arrays.asList(Amenity.WIFI))
                .address("neka adresa")
                .maxGuests(1L)
                .minGuests(1L)
                .name("neka akom")
                .description("neka deskripcija")
                .lat(45.0)
                .lon(45.0)
                .accommodationType(AccommodationType.STUDIO)
                .autoAcceptEnabled(false)
                .availabilityList(List.of(avail, avail1))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccommodationDTO> requestEntity = new HttpEntity<>(accommodationDTO, headers);




        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + "edit/accommodation/" + 1L,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(acceptResponse.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String message = jsonNode.get("message").asText();


        System.out.println(acceptResponse);

        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "Availability with start date 2024-03-04 and end date 2024-04-03 interlaps with " +
                "availability with start date 2024-03-04 and end date 2024-04-03");
    }

    @Test
    public void shouldThrowConflictedSuccess(){
        setInitialData();


//        AccommodationAvailabilityDTO avail = AccommodationAvailabilityDTO.builder()
//                .startDate(LocalDate.now().plusDays(40))
//                .endDate(LocalDate.now().plusDays(70))
//                .price(250L)
//                .pricePerGuest(true)
//                .cancellationDeadline(LocalDate.now().plusDays(8))
//                .build();

        AccommodationAvailabilityDTO avail1 = AccommodationAvailabilityDTO.builder()
                .id(1L)
                .startDate(LocalDate.now().plusDays(40))
                .endDate(LocalDate.now().plusDays(70))
                .price(250L)
                .pricePerGuest(true)
                .cancellationDeadline(LocalDate.now().plusDays(8))
                .build();


        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .amenities(Arrays.asList(Amenity.WIFI))
                .address("neka adresa")
                .maxGuests(1L)
                .minGuests(1L)
                .name("neka akom")
                .description("neka deskripcija")
                .lat(45.0)
                .lon(45.0)
                .accommodationType(AccommodationType.STUDIO)
                .autoAcceptEnabled(false)
                .availabilityList(List.of(avail1))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AccommodationDTO> requestEntity = new HttpEntity<>(accommodationDTO, headers);




        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + "edit/accommodation/" + 1L,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );





        assertEquals(acceptResponse.getStatusCode().value(), 200);
        assertEquals(acceptResponse.getBody(), "Successfully created new accommodation request");
    }

}
