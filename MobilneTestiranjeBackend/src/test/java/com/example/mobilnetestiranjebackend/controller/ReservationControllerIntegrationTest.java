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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(
//        locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ReservationControllerIntegrationTest {
    private final String BASE_PATH = "http://localhost:8080/api/v1/accommodation";
    private static final String OWNER_EMAIL = "probamejl@gmail.com";
    private static final String OWNER_PASSWORD = "NekaSifra123";
    private String ownerToken = null;

    private static final String GUEST_EMAIL = "probamejl1@gmail.com";
    private static final String GUEST_PASSWORD = "NekaSifra123";
    private String guestToken = null;


    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate driverRestTemplate, passengerRestTemplate;

    HttpHeaders headers = new HttpHeaders();

    public void setInitialData(){
        headers.setContentType(MediaType.APPLICATION_JSON);
        loginAsDriver();
        loginAsPassenger();
        createRestTemplatesForUsers();

    }

    private void loginAsDriver() {
        HttpEntity<AuthenticationRequestDTO> driverLogin = new HttpEntity<>(new AuthenticationRequestDTO(OWNER_EMAIL, OWNER_PASSWORD), headers);

        ResponseEntity<AuthenticationResponseDTO> driverResponse = restTemplate
                .exchange("/api/v1/auth/authenticate",
                        HttpMethod.POST,
                        driverLogin,
                        new ParameterizedTypeReference<AuthenticationResponseDTO>() {
                        });

        this.ownerToken = driverResponse.getBody().getToken();
    }

    private void loginAsPassenger() {
        HttpEntity<AuthenticationRequestDTO> passengerLogin = new HttpEntity<>(new AuthenticationRequestDTO(GUEST_EMAIL, GUEST_PASSWORD), headers);

        ResponseEntity<AuthenticationResponseDTO> passengerResponse = restTemplate
                .exchange("/api/v1/auth/authenticate",
                        HttpMethod.POST,
                        passengerLogin,
                        new ParameterizedTypeReference<AuthenticationResponseDTO>() {
                        });

        this.guestToken = passengerResponse.getBody().getToken();
    }

    private void createRestTemplatesForUsers() {
        RestTemplateBuilder builder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-Auth-Token", this.ownerToken);
            return execution.execute(request, body);
        }));
        this.driverRestTemplate = new TestRestTemplate(builder);

        RestTemplateBuilder passBuilder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("X-Auth-Token", this.ownerToken);
            return execution.execute(request, body);
        }));

        this.passengerRestTemplate = new TestRestTemplate(passBuilder);
    }


    @Test
    public void shouldRetrieveAllComment() {
        setInitialData();
    }
}