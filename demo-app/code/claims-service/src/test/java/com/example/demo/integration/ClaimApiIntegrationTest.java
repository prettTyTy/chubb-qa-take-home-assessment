package com.example.demo.integration;

import com.example.demo.ClaimsServiceApplication;
import com.example.demo.domain.claim.ClaimRepository;
import com.example.demo.user.domain.Email;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserId;
import com.example.demo.user.domain.UserRepository;
import com.example.demo.user.domain.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest(classes = ClaimsServiceApplication.class)
@AutoConfigureMockMvc
class ClaimApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveClaimThroughApi() throws Exception {

        UUID userId = UUID.randomUUID();

        User user = new User(
                UserId.of(userId),
                "Integration Test User",
                Email.of("integration." + userId + "@example.com"),
                UserRole.CLAIMANT,
                Instant.now()
        );

        userRepository.save(user);

        int claimsBefore = claimRepository.findAll().size();

        String requestBody = """
                {
                    "incidentDate": "2026-09-18",
                    "incidentLocation": "123 Main Street",
                    "description": "Testing claim submission through API",
                    "claimAmount": 5000.00
                }
                """;

        String response = mockMvc.perform(
                        post("/api/claims")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject(userId.toString())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String claimId = objectMapper.readTree(response)
                .get("claimId")
                .asText();

        var claims = claimRepository.findAll();

        assertEquals(claimsBefore + 1, claims.size());

        var createdClaim = claims.stream()
                .filter(claim -> claim.getUserId().equals(userId))
                .findFirst()
                .orElseThrow();

        assertEquals(userId, createdClaim.getUserId());
        assertEquals(
                new BigDecimal("5000.00"),
                createdClaim.getClaimAmount()
        );
        assertEquals(
                LocalDate.of(2026, 9, 18),
                createdClaim.getIncidentDate()
        );

        mockMvc.perform(
                        get("/api/claims/" + claimId)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject(userId.toString())))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimId").value(claimId))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.incidentDate").value("2026-09-18"))
                .andExpect(jsonPath("$.incidentLocation").value("123 Main Street"))
                .andExpect(jsonPath("$.claimAmount").value(5000.0))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }
}