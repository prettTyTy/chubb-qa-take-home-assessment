package com.example.demo.domain.claim;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClaimTest {

    @Test
    void shouldCreateValidClaim() {
        UUID claimId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Claim claim = new Claim(
                claimId,
                userId,
                LocalDate.now(),
                "123 Main Street",
                "Testing valid claim",
                new BigDecimal("5000")
        );

        assertEquals(claimId, claim.getClaimId());
        assertEquals(userId, claim.getUserId());
        assertEquals(ClaimStatus.SUBMITTED, claim.getStatus());
        assertEquals(new BigDecimal("5000"), claim.getClaimAmount());
        assertEquals("123 Main Street", claim.getIncidentLocation());
        assertEquals("Testing valid claim", claim.getDescription());
    }

@Test
void shouldRejectDescriptionShorterThan10Characters() {
    UUID claimId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    assertThrows(
            InvalidClaimException.class,
            () -> new Claim(
                    claimId,
                    userId,
                    LocalDate.now(),
                    "123 Main Street",
                    "Too short",
                    new BigDecimal("5000")
            )
    );
}

@Test
void shouldRejectClaimAmountAboveMaximum() {
    UUID claimId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    assertThrows(
            InvalidClaimException.class,
            () -> new Claim(
                    claimId,
                    userId,
                    LocalDate.now(),
                    "123 Main Street",
                    "Testing claim amount",
                    new BigDecimal("1000001")
            )
    );
}

@Test
void shouldRejectFutureIncidentDate() {
    UUID claimId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    assertThrows(
            InvalidClaimException.class,
            () -> new Claim(
                    claimId,
                    userId,
                    LocalDate.now().plusDays(1),
                    "123 Main Street",
                    "Testing future date",
                    new BigDecimal("5000")
            )
    );
}

@Test
void shouldAllowValidStatusTransition() {
    Claim claim = new Claim(
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDate.now(),
            "123 Main Street",
            "Testing status transition",
            new BigDecimal("5000")
    );

    claim.updateStatus(ClaimStatus.UNDER_REVIEW);

    assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());
}

@Test
void shouldRejectInvalidStatusTransition() {
    Claim claim = new Claim(
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDate.now(),
            "123 Main Street",
            "Testing invalid status",
            new BigDecimal("5000")
    );

    assertThrows(
            InvalidStatusTransitionException.class,
            () -> claim.updateStatus(ClaimStatus.APPROVED)
    );
}
}