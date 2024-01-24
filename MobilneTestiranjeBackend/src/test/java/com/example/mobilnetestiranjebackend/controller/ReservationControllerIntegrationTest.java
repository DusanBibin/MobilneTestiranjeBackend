package com.example.mobilnetestiranjebackend.controller;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

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
                BASE_PATH + 1 + "/reservation/" + 1 + "/accept",
                HttpMethod.PUT,
                null,
                new ParameterizedTypeReference<String>() {
                }
        );

        assertEquals(acceptResponse.getStatusCode().value(), 200);
        assertEquals(acceptResponse.getBody(), "Successfully accepted a reservation request");
    }


}