package com.sangeetha.booking_service;

import com.sangeetha.booking_service.dto.EventRequest;
import com.sangeetha.booking_service.dto.EventResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class EventControllerIntegrationTest {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/events";
    }

    @Test
    void createEvent_withValidData_returnsCreatedEvent() {
        EventRequest request = new EventRequest();
        request.setName("Integration Test Event");
        request.setDate(LocalDateTime.now().plusDays(30));
        request.setTotalTickets(50);

        ResponseEntity<EventResponse> response =
                restTemplate.postForEntity(baseUrl(), request, EventResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Integration Test Event");
        assertThat(response.getBody().getTicketsRemaining()).isEqualTo(50);
    }

    @Test
    void createEvent_withBlankName_returns400() {
        EventRequest request = new EventRequest();
        request.setName("");
        request.setDate(LocalDateTime.now().plusDays(30));
        request.setTotalTickets(50);

        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl(), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getEventById_whenNotFound_returns404() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl() + "/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}