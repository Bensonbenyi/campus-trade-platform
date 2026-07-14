package com.campus.trade.common.utils;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {
    private static final String SECRET = "a-test-secret-that-is-at-least-thirty-two-bytes";

    @Test
    void createsAndParsesExpectedClaims() {
        JwtUtils jwtUtils = new JwtUtils(
                SECRET,
                60_000,
                Clock.fixed(Instant.parse("2026-07-14T12:00:00Z"), ZoneOffset.UTC));

        String token = jwtUtils.generateToken(42L, "admin");

        assertTrue(jwtUtils.isValid(token));
        assertEquals(42L, jwtUtils.getUserId(token));
        assertEquals("ADMIN", jwtUtils.getRole(token));
    }

    @Test
    void rejectsTamperedTokens() {
        JwtUtils jwtUtils = new JwtUtils(SECRET, 60_000, Clock.systemUTC());
        String token = jwtUtils.generateToken(1L, "USER");

        assertFalse(jwtUtils.isValid(token.substring(0, token.length() - 1) + "x"));
    }
}
