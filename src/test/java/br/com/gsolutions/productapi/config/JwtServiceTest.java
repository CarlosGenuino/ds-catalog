package br.com.gsolutions.productapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractUsername() {
        // Gera um token válido
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        // Extrai o nome de usuário do token
        String username = jwtService.extractUsername(token);

        // Verifica se o nome de usuário está correto
        assertEquals("user@example.com", username);
    }

    @Test
    void testGenerateTokenWithClaims() {
        // Cria claims personalizados
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        // Gera um token com claims
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(claims, userDetails);

        // Verifica se o token foi gerado
        assertNotNull(token);
    }

    @Test
    void testGenerateTokenWithoutClaims() {
        // Gera um token sem claims
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        // Verifica se o token foi gerado
        assertNotNull(token);
    }

    @Test
    void testIsTokenValid() {
        // Gera um token válido
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        // Verifica se o token é válido
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Verifica se o token é válido
        assertTrue(isValid);
    }

    @Test
    void testIsTokenInvalid() {
        // Gera um token válido
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        // Altera o nome de usuário no UserDetails para simular um token inválido
        when(userDetails.getUsername()).thenReturn("anotheruser@example.com");

        // Verifica se o token é inválido
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Verifica se o token é inválido
        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired() {
        // Configura o UserDetails
        when(userDetails.getUsername()).thenReturn("user@example.com");

        // Gera um token com uma data de expiração no passado
        String token = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // 24 horas atrás
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12)) // 12 horas atrás
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        // Verifica se o token está expirado
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testExtractClaim() {
        // Gera um token válido
        when(userDetails.getUsername()).thenReturn("user@example.com");
        String token = jwtService.generateToken(userDetails);

        // Extrai a data de expiração do token
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Verifica se a data de expiração foi extraída corretamente
        assertNotNull(expiration);
    }
}