package br.com.gsolutions.productapi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gsolutions.productapi.token.Token;
import br.com.gsolutions.productapi.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    public void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    public void whenValidJwtToken_thenAuthenticateUser() throws Exception {
        // Given
        String jwtToken = "valid-jwt-token";
        String userEmail = "user@example.com";
        UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());

        request.addHeader("Authorization", "Bearer " + jwtToken);

        when(jwtService.extractUsername(jwtToken)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);
        when(tokenRepository.findByToken(jwtToken))
                .thenReturn(Optional.of(new Token(jwtToken, false, false)));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(jwtToken);
        verify(userDetailsService).loadUserByUsername(userEmail);
        verify(jwtService).isTokenValid(jwtToken, userDetails);
        verify(tokenRepository).findByToken(jwtToken);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(userEmail);
    }

    @Test
    public void whenInvalidJwtToken_thenDoNotAuthenticate() throws Exception {
        // Given
        String jwtToken = "invalid-jwt-token";
        request.addHeader("Authorization", "Bearer " + jwtToken);

        when(jwtService.extractUsername(jwtToken)).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(jwtToken);
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(tokenRepository);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void whenNoAuthorizationHeader_thenDoNotAuthenticate() throws Exception {
        // Given (no Authorization header)

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(tokenRepository);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void whenExpiredOrRevokedToken_thenDoNotAuthenticate() throws Exception {
        // Given
        String jwtToken = "expired-jwt-token";
        String userEmail = "user@example.com";
        UserDetails userDetails = new User(userEmail, "password", Collections.emptyList());

        request.addHeader("Authorization", "Bearer " + jwtToken);

        when(jwtService.extractUsername(jwtToken)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);
        when(tokenRepository.findByToken(jwtToken))
                .thenReturn(Optional.of(new Token(jwtToken, true, true))); // Expired and revoked

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(jwtToken);
        verify(userDetailsService).loadUserByUsername(userEmail);
        verify(jwtService).isTokenValid(jwtToken, userDetails);
        verify(tokenRepository).findByToken(jwtToken);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}