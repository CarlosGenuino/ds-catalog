package br.com.gsolutions.productapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;
    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTY5NzU2ODAwMCwiZXhwIjoxNjk3NjU0NDAwfQ.1Q2w3e4r5t6y7u8i9o0p";
    private static final String SECRET_KEY = "4D6251655468576D597133743677397A24432646294A404E635266556A586E32";
    @Test
    void testExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals(USERNAME, username);
    }

    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void testExtractClaim() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        Function<Claims, String> claimsResolver = Claims::getSubject;

        // Act
        String subject = jwtService.extractClaim(token, claimsResolver);

        // Assert
        assertEquals(USERNAME, subject);
    }

    @Test
    void testGenerateTokenWithClaims() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        // Act
        String token = jwtService.generateToken(claims, userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateTokenWithoutClaims() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testIsTokenValid() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() {
        // Arrange
        String expiredToken = Jwts.builder()
                .setSubject(USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 48))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // Token expirado
                .signWith(getSignInKey())
                .compact();

        // Act
        boolean isExpired = jwtService.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenNotExpired() {
        // Arrange
        String validToken = Jwts.builder()
                .setSubject(USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido
                .signWith(getSignInKey())
                .compact();

        // Act
        boolean isExpired = jwtService.isTokenExpired(validToken);

        // Assert
        assertFalse(isExpired);
    }

    private Key getSignInKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}