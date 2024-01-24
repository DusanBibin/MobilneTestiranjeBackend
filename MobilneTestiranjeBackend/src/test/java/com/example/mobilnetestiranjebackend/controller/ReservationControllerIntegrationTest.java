package com.example.mobilnetestiranjebackend.controller;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@TestPropertySource(
//        locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ReservationControllerIntegrationTest {
    private final String BASE_PATH = "http://localhost:8080/api/v1/accommodation/";
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
    public void shouldAcceptReservation() {
        setInitialData();
        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + 1 + "/reservation/" + 2 + "/accept",
                HttpMethod.PUT,
                null,
                new ParameterizedTypeReference<String>() {
                }
        );
        assertEquals(acceptResponse.getStatusCode().value(), 200);
        assertEquals(acceptResponse.getBody(), "Successfully accepted a reservation request");
    }

    @Test
    public void shouldThrowAccommodationIdError() {
        setInitialData();
        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + 500 + "/reservation/" + 1 + "/accept",
                HttpMethod.PUT,
                null,
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



        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "Accommodation with this id doesn't exist");
    }

    @Test
    public void shouldThrowReservationIdError() {
        setInitialData();
        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + 1 + "/reservation/" + 100 + "/accept",
                HttpMethod.PUT,
                null,
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


        System.out.println(message);
        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "Reservation with this id doesn't exist");
    }

    @Test
    public void shouldThrowNotPendingError() {
        setInitialData();
        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + 1 + "/reservation/" + 1 + "/accept",
                HttpMethod.PUT,
                null,
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


        System.out.println(message);
        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "You can only accept a pending request reservation");
    }

    @Test
    public void shouldThrowNotPending() {
        setInitialData();
        ResponseEntity<String> acceptResponse = this.ownerRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/" + 2 + "/accept",
                HttpMethod.PUT,
                null,
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


        System.out.println(message);
        assertEquals(acceptResponse.getStatusCode().value(), 400);
        assertEquals(message, "You cannot do action for a accommodation you don't own");
    }



    @Test
    public void shouldCreateReservation() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(1L)
                .availabilityId(2L)
                .reservationStartDate(LocalDate.now().plusDays(16))
                .reservationEndDate(LocalDate.now().plusDays(19))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<String>() {
                }
        );

        System.out.println(acceptResponse);
        assertEquals(acceptResponse.getStatusCode().value(), 200);
        assertEquals(acceptResponse.getBody(), "Successfully created new reservation request");

    }

    @Test
    public void shouldThrowAccommodationIdErrorCreate() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(500L)
                .availabilityId(1L)
                .reservationStartDate(LocalDate.now().plusDays(16))
                .reservationEndDate(LocalDate.now().plusDays(19))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
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
        assertEquals(message, "Accommodation with this id doesn't exist");
    }


    @Test
    public void shouldThrowAvailabilityIdErrorCreate() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(2L)
                .availabilityId(500L)
                .reservationStartDate(LocalDate.now().plusDays(16))
                .reservationEndDate(LocalDate.now().plusDays(19))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
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
        assertEquals(message, "Availability with this id for wanted accommodation doesn't exist");
    }


    @Test
    public void shouldThrowStartDateOutOfRange() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(1L)
                .availabilityId(2L)
                .reservationStartDate(LocalDate.now().plusDays(13))
                .reservationEndDate(LocalDate.now().plusDays(19))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
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
        assertEquals(message, "Start date is out of range for availability period");
    }

    @Test
    public void shouldThrowEndDateOutOfRange() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(1L)
                .availabilityId(2L)
                .reservationStartDate(LocalDate.now().plusDays(17))
                .reservationEndDate(LocalDate.now().plusDays(23))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
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
        assertEquals(message, "End date is out of range for availability period");
    }

    @Test
    public void shouldThrowDateRangeAlreadyTaken() {
        setInitialData();

        ReservationDTO reservationDTO =ReservationDTO.builder()
                .accommodationId(2L)
                .availabilityId(4L)
                .reservationStartDate(LocalDate.now().plusDays(31))
                .reservationEndDate(LocalDate.now().plusDays(39))
                .guestNum(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReservationDTO> requestEntity = new HttpEntity<>(reservationDTO, headers);




        ResponseEntity<String> acceptResponse = this.guestRestTemplate.exchange(
                BASE_PATH + 2 + "/reservation/create",
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
        assertEquals(message, "There is already an accepted reservation for this date range");
    }
}